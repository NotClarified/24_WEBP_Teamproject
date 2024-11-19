package ch10;

import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;
import org.apache.commons.beanutils.BeanUtils;

@WebServlet("/news.nhn") 
@MultipartConfig(maxFileSize = 1024 * 1024 * 2)
// @MultipartConfig(maxFileSize = 1024 * 1024 * 2, location = "C:\Temp\img") 
public class NewsController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private NewsDAO dao;
    private ServletContext ctx;
    
    private final String START_PAGE = "ch10/newsList.jsp";

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        dao = new NewsDAO();
        ctx = getServletContext();
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String action = request.getParameter("action");
        
        dao = new NewsDAO();
        
        Method m;
        String view = null;
        if (action == null) {
            action = "listNews";
        }

        try {
        	// 현재 클래스에서 action 이름과 HttpServletRequest 를 파라미터로 하는 메서드 찾음
        	m = this.getClass().getMethod(action, HttpServletRequest.class);
        	// 메서드 실행후 리턴값 받아옴
        	view = (String)m.invoke(this, request);
        } catch (NoSuchMethodException e) {
        	e.printStackTrace();
        	// 에러 로그를 남기고 view 를 로그인 화면으로 지정, 앞에서와 같이 redirection 사용도 가능.
        	ctx.log("요청 action 없음!!");
        	request.setAttribute("error", "action 파라미터가 잘못 되었습니다!!");
        	view = "ch10/newsList.jsp"; // START_PAGE 대신 하드코딩함 
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        if(view.startsWith("redirect:/")) {
        	// redirect:/ 문자열 이후 경로만 가지고 옴
        	String rview = view.substring("redirect:/".length());
        	response.sendRedirect(rview);
        } else {
        	// 지정된 뷰로 포워딩, 포워딩시 컨텍스트경로는 필요없음.
        	RequestDispatcher dispatcher = request.getRequestDispatcher(view);
        	dispatcher.forward(request, response);
        }
    }

    public String addNews(HttpServletRequest request) {
    	News n = new News();
    	try {
    	// 이미지 파일 저장
    	 Part part = request.getPart("file");
    	String fileName = getFilename(part);
    	if(fileName != null && !fileName.isEmpty()){
    	part.write(fileName);
    	}
    	
    	// 입력값을 News 객체로 매핑
    	BeanUtils.populate(n, request.getParameterMap());
    	
    	// 이미지 파일 이름을 News 객체에도 저장
    	 n.setImg("/img/"+fileName);
    	 
    	dao.addNews(n);
    	} catch (Exception e) {
    	e.printStackTrace();
    	ctx.log("뉴스 추가 과정에서 문제 발생!!");
    	request.setAttribute("error", "뉴스가 정상적으로 등록되지 않았습니다!!");
    	return listNews(request);
    	}
    	
    	return "redirect:/news.nhn?action=listNews"; 
    	//“redirect:/” 이후에 오는 경로 부분에서 “프로젝트명” 다음에 오는 경로만 작성

    }
    
    private String getFilename(Part part) {
        String header = part.getHeader("content-disposition");
        String fileName = header.substring(header.indexOf("filename=") + 10, header.length() - 1);
        ctx.log("File name: " + fileName);
        return fileName;
    }

    public String listNews(HttpServletRequest request) {
        ctx.log("listNews 메서드 호출됨."); // 메서드 호출 확인용 로그
        try {
            List<News> list = dao.getAll();
            request.setAttribute("newslist", list);
        } catch (Exception e) {
            e.printStackTrace();
            ctx.log("Error generating news list.");
            request.setAttribute("error", "Failed to load news list.");
        }
        return "ch10/newsList.jsp";
    }

    public String getNews(HttpServletRequest request) {
        int aid = Integer.parseInt(request.getParameter("aid"));
        try {
            News n = dao.getNews(aid);
            request.setAttribute("news", n);
        } catch (SQLException e) {
            e.printStackTrace();
            ctx.log("Error fetching news.");
            request.setAttribute("error", "Failed to fetch news.");
        }
        return "ch10/newsView.jsp";
    }

    public String deleteNews(HttpServletRequest request) {
        int aid = Integer.parseInt(request.getParameter("aid"));
        try {
            dao.delNews(aid);
        } catch (SQLException e) {
            e.printStackTrace();
            ctx.log("Error deleting news.");
            request.setAttribute("error", "Failed to delete news.");
            return listNews(request);
        }
        return "redirect:/news.nhn?action=listNews";
    }
    //뉴스 업데이트
    public String updateNews(HttpServletRequest request) {
        News n = new News();
        try {
            // 입력값을 News 객체로 매핑
            BeanUtils.populate(n, request.getParameterMap());

            // 이미지 파일 저장
            Part part = request.getPart("file");
            String fileName = getFilename(part);
            if (fileName != null && !fileName.isEmpty()) {
                // 파일 저장
                part.write(fileName);
                n.setImg("/img/" + fileName);
            }

            // 뉴스 업데이트 실행
            dao.updateNews(n);
        } catch (Exception e) {
            e.printStackTrace();
            ctx.log("뉴스 수정 과정에서 문제 발생!!");
            request.setAttribute("error", "뉴스가 정상적으로 수정되지 않았습니다!!");
            return listNews(request);
        }
        
        return "redirect:/news.nhn?action=listNews";
    }
    
    public String editNews(HttpServletRequest request) {
        int aid = Integer.parseInt(request.getParameter("aid"));
        try {
            News news = dao.getNews(aid);
            request.setAttribute("news", news);
        } catch (SQLException e) {
            e.printStackTrace();
            ctx.log("Error fetching news for edit.");
            request.setAttribute("error", "Failed to fetch news for editing.");
        }
        return "ch10/newsEdit.jsp";
    }

}

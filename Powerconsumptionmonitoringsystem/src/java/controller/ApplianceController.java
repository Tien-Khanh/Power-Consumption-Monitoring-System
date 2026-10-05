/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.ApplianceDao;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Appliance;

/**
 *
 * @author hoanh
 */
@WebServlet(name = "ApplianceController", urlPatterns = {"/ApplianceController"})
public class ApplianceController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ApplianceDao dao = new ApplianceDao();
        List<Appliance> list = dao.getALLAppliances();

        // Đặt danh sách vào request với tên "applianceList"
        request.setAttribute("applianceList", list);

        // Chuyển tiếp sang trang hiển thị
        request.getRequestDispatcher("nilm_list.jsp").forward(request, response);
    }
}

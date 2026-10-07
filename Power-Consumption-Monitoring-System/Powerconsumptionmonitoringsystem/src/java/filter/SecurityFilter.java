/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.User;
import dao.PermissionDao;
import util.SecurityUtils;

@WebFilter(urlPatterns = {"/*"})
public class SecurityFilter implements Filter {
    private static final Set<String> DEVICE_API_PATHS = new HashSet<String>(Arrays.asList(
            "/api/nilm/batch", "/api/health"));
    private static final Map<String, String> RESOURCE_BY_PATH = new HashMap<String, String>();
    static {
        RESOURCE_BY_PATH.put("/ApplianceController", "APPLIANCE");
        RESOURCE_BY_PATH.put("/ApplianceCycleController", "APPLIANCECYCLE");
        RESOURCE_BY_PATH.put("/UserController", "USER");
        RESOURCE_BY_PATH.put("/AuditController", "AUDIT");
        RESOURCE_BY_PATH.put("/AlertController", "ALERT");
    }
 
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getServletPath();
 
        if (SecurityUtils.PUBLIC_PATHS.contains(path)
                || path.startsWith("/css/") || path.startsWith("/js/")) {
            chain.doFilter(request, response);
            return;
        }
        if (DEVICE_API_PATHS.contains(path)) {
            if (SecurityUtils.isValidDeviceKey(req.getHeader("X-API-Key"))) {
                chain.doFilter(request, response);
            } else {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API key");
            }
            return;
        }
        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("LOGIN_USER");
        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        String resource = RESOURCE_BY_PATH.get(path);
        String action = "GET".equals(req.getMethod()) ? "READ" : "WRITE";
        if (resource != null && !new PermissionDao().hasPermission(user.getUserId(), resource, action)) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Permission denied");
            return;
        }
        chain.doFilter(request, response);
    }
}

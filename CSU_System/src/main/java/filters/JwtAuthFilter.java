package filters;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.JwtUtil;

import java.io.IOException;

@WebFilter(filterName = "JwtAuthFilter", urlPatterns = "/api/*")
public class JwtAuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(req, res);
            return;
        }

        String ctx = request.getContextPath(); // p.ej. "/CSU_System"
        String path = request.getRequestURI();

        boolean isWhitelisted =
                path.contains("/login") ||
                        path.contains("/register") ||
                        path.equals(ctx + "/api/openapi") ||
                        path.startsWith(ctx + "/api/openapi/") ||
                        path.startsWith(ctx + "/resources/swagger-ui/") ||
                        path.contains("/api/organizations") ||
                        path.contains("/profile") ||
                        path.startsWith(ctx + "/api/reports/public") ||
                        (path.startsWith(ctx + "/api/reports/") && path.endsWith("/file"));

        if (isWhitelisted) {
            chain.doFilter(req, res);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Falta token\"}");
            return;
        }

        String token = authHeader.substring("Bearer".length()).trim();
        try {
            Claims claims = JwtUtil.getClaims(token);
            request.setAttribute("userId", claims.getSubject());
            request.setAttribute("role", claims.get("role"));
            request.setAttribute("enabled", claims.get("enabled"));
            chain.doFilter(req, res);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Token inválido\"}");
        }
    }
}

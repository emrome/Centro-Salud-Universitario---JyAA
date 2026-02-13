package interceptors;

import exceptions.ForbiddenException;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.servlet.http.HttpServletRequest;

@Interceptor
@HealthOnly
@Priority(Interceptor.Priority.APPLICATION)
public class HealthOnlyInterceptor {

    @Inject
    private HttpServletRequest request;

    @AroundInvoke
    public Object checkHealth(InvocationContext ctx) throws Exception {
        String role = (String) request.getAttribute("role");
        if (role == null) throw new ForbiddenException("Access denied: No role found.");
        if (!"HealthStaff".equals(role)) throw new ForbiddenException("Access denied: HealthStaff role required.");
        return ctx.proceed();
    }
}


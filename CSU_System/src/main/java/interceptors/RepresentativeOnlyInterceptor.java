package interceptors;

import exceptions.ForbiddenException;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.servlet.http.HttpServletRequest;

@Interceptor
@RepresentativeOnly
@Priority(Interceptor.Priority.APPLICATION)
public class RepresentativeOnlyInterceptor {

    @Inject
    private HttpServletRequest request;

    @AroundInvoke
    public Object checkRepresentative(InvocationContext ctx) throws Exception {
        String role = (String) request.getAttribute("role");
        if (role == null) throw new ForbiddenException("Access denied: No role found.");
        if (!"SocialOrgRepresentative".equals(role)) throw new ForbiddenException("Access denied: SocialOrgRepresentative role required.");
        return ctx.proceed();
    }
}

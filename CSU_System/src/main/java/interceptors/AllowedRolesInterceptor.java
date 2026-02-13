package interceptors;

import exceptions.ForbiddenException;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@AllowedRoles
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class AllowedRolesInterceptor {

    @Inject
    private HttpServletRequest request;

    @AroundInvoke
    public Object checkRoles(InvocationContext ctx) throws Exception {
        AllowedRoles ann = ctx.getMethod().getAnnotation(AllowedRoles.class);
        if (ann == null) {
            ann = ctx.getTarget().getClass().getAnnotation(AllowedRoles.class);
        }

        if (ann == null) {
            return ctx.proceed();
        }

        Set<String> required = Arrays.stream(ann.value())
                .filter(r -> r != null && !r.isBlank())
                .collect(Collectors.toSet());

        if (required.isEmpty()) {
            throw new ForbiddenException("Access denied: roles not configured.");
        }

        String role = (String) request.getAttribute("role");
        if (role == null || role.isBlank()) {
            throw new ForbiddenException("Access denied: No role found.");
        }

        if (!required.contains(role)) {
            throw new ForbiddenException("Access denied: insufficient role.");
        }

        return ctx.proceed();
    }
}

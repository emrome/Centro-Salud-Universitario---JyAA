package interceptors;

import jakarta.interceptor.InterceptorBinding;
import jakarta.enterprise.util.Nonbinding;

import java.lang.annotation.*;

@InterceptorBinding
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowedRoles {
    @Nonbinding String[] value() default {}; // Admin, HealthStaff, etc.
}


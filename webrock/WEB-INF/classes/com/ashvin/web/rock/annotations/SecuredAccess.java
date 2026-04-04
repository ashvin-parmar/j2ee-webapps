package com.ashvin.web.rock.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface SecuredAccess 
{
public String checkPost() default "";
public String guard() default "";
}

package com.ashvin.web.rock.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ONSTARTUP
{
public int priority() default 0;
}

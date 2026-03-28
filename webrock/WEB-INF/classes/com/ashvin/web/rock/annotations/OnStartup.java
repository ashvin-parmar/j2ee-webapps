package com.ashvin.web.rock.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnStartup
{
public int priority() default 0;
}

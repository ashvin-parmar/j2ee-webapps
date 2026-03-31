package com.ashvin.web.rock.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InjectRequestParameter
{
public String value() default "";
}

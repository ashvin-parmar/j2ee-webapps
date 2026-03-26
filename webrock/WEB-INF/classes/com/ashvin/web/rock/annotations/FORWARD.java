package com.ashvin.web.rock.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface FORWARD
{
public String value() default "";
//String relativeValue() default "";    //Idea to implemet this feature has been dropped
}

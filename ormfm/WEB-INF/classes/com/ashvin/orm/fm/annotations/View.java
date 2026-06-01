package com.ashvin.orm.fm.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface View
{
public String name();
public boolean readOnly() default true;
}

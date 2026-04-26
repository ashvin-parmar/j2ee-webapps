package com.ashvin.orm.fm.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ForeignKey
{
public String parent() default "";
public String column() default "";
}

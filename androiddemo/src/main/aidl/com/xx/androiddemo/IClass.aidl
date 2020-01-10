// IClass.aidl
package com.xx.androiddemo;

// Declare any non-default types here with import statements
import com.xx.androiddemo.Student;

interface IClass {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);

    void addStudent(in Student student);
    Student findStudent(in String name);
}

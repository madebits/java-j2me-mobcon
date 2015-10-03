package vdoclet.docinfo;

import junit.framework.TestCase;
import java.util.*;

public class MethodInfo_Test extends TestCase
{
    //---( Setup )---

    public MethodInfo_Test( String name ) {
        super( name );
    }

    //---( Tests )---

    public void testCreate() {
        MethodInfo methodInfo = new MethodInfo( "int", "doSomething" );
        assertEquals( "doSomething", methodInfo.getName() );
        assertEquals( "int", methodInfo.getType() );
    }

    public void testClone() {
        MethodInfo methodInfo = new MethodInfo( "int", "doSomething" );
        MethodInfo methodInfo2 = (MethodInfo) methodInfo.clone();
        assertSame( methodInfo.getType(), methodInfo2.getType() );
        assertEquals( methodInfo.getParameters(), 
                      methodInfo2.getParameters() );
        assertEquals( methodInfo.getExceptions(), 
                      methodInfo2.getExceptions() );
    }

    public void testCloneDeepCopy() {
        MethodInfo methodInfo = new MethodInfo( "int", "doSomething" );
        MethodInfo methodInfo2 = (MethodInfo) methodInfo.clone();
        methodInfo2.addParameter( "int", "size" );
        methodInfo2.addException( "HeapBigProblem" );
        assertTrue( !methodInfo.getExceptions()
                    .equals( methodInfo2.getExceptions() ));
        assertTrue( !methodInfo.getExceptions()
                    .equals( methodInfo2.getExceptions() ));
    }

    public void testAddParameter() {
        MethodInfo methodInfo = new MethodInfo( "int", "doSomething" );
        ParameterInfo param = new ParameterInfo( "java.lang.String", "fish" );
        methodInfo.addParameter( param );
        assertEquals( true, methodInfo.getParameters().contains( param ));
    }

    public void testGetParametersString() {
        MethodInfo methodInfo = new MethodInfo( "int", "doSomething" );
        ParameterInfo param = new ParameterInfo( "int", "fish" );
        methodInfo.addParameter( new ParameterInfo( "int", "fish" ));
        methodInfo.addParameter( new ParameterInfo( "int", "oven" ));
        assertEquals( "int fish, int oven", 
                      methodInfo.getParameters().toString() );
    }

    public void testAddException() {
        MethodInfo methodInfo = new MethodInfo( "int", "doSomething" );
        String ex = "java.lang.IllegalArgumentException";
        methodInfo.addException( ex );
        assertTrue( methodInfo.getExceptions().contains( ex ));
    }

    public void testAddExceptionDups() {
        MethodInfo methodInfo = new MethodInfo( "int", "doSomething" );
        methodInfo.addException( "E2" );
        methodInfo.addException( "E2" );
        methodInfo.addException( "E1" );
        assertEquals( 2, methodInfo.getExceptions().size() );
    }
    
    public void testGetExceptionsString() {
        MethodInfo methodInfo = new MethodInfo( "int", "doSomething" );
        methodInfo.addException( "E2" );
        methodInfo.addException( "E1" );
        assertEquals( "E1, E2", methodInfo.getExceptions().toString() );
    }

    public void testSetAbstract() {   
        MethodInfo info = new MethodInfo( "int", "doSomething" );
        assertTrue( !info.isAbstract() );
        info.setAbstract( true );
        assertTrue( info.isAbstract() );
        assertEquals( "abstract", info.getModifiers().toString() );
    }

    public void testSetPublic() {
        MethodInfo info = new MethodInfo( "int", "doSomething" );
        assertTrue( !info.isPublic() );
        info.setPublic( true );
        assertTrue( info.isPublic() );
        assertEquals( "public", info.getModifiers().toString() );
    }

    public void testParameterTypes() {
        MethodInfo m = new MethodInfo( "int", "doSomething" );
        m.addParameter( "int", "foo" );
        m.addParameter( "java.lang.Object", "bar" );
        List paramTypes = new ArrayList();
        paramTypes.add( "int" );
        paramTypes.add( "java.lang.Object" );
        assertEquals( paramTypes, m.getParameterTypes() );
        assertEquals( "int,java.lang.Object", m.getParameterTypes().toString() );
    }

    public void testParameterNames() {
        MethodInfo m = new MethodInfo( "int", "doSomething" );
        m.addParameter( "int", "foo" );
        m.addParameter( "java.lang.Object", "bar" );
        List paramNames = new ArrayList();
        paramNames.add( "foo" );
        paramNames.add( "bar" );
        assertEquals( paramNames, m.getParameterNames() );
        assertEquals( "foo,bar", m.getParameterNames().toString() );
    }

}

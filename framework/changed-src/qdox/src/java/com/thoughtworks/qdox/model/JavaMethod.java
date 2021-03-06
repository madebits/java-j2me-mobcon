package com.thoughtworks.qdox.model;

public class JavaMethod extends AbstractJavaEntity {

    protected Type returns;
    private JavaParameter[] parameters = JavaParameter.EMPTY_ARRAY;
    private Type[] exceptions = Type.EMPTY_ARRAY;
    private boolean constructor;

    private JavaClass parentClass;

    public Type getReturns() {
        return returns;
    }

    public JavaParameter[] getParameters() {
        return parameters;
    }

    public JavaParameter getParameterByName(String name) {
        JavaParameter[] parameters = getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getName().equals(name)) {
                return parameters[i];
            }
        }
        return null;
    }

    public Type[] getExceptions() {
        return exceptions;
    }

    public boolean isConstructor() {
        return constructor;
    }

    protected void writeBody(IndentBuffer result) {
        writeBody(result, true, true, true);
    }

    /**
     * @since 1.3
     */
    protected void writeBody(IndentBuffer result, boolean withModifiers, boolean isDeclaration, boolean isPrettyPrint) {
        if (withModifiers) {
            writeAccessibilityModifier(result);
            writeNonAccessibilityModifiers(result);
        }

        if (!constructor) {
            if(isDeclaration) {
                result.write(returns.toString());
                result.write(' ');
            }
        }

        result.write(name);
        result.write('(');
        for (int i = 0; i < parameters.length; i++) {
            JavaParameter parameter = parameters[i];
            if (i > 0) result.write(", ");
            if (isDeclaration) {
                result.write(parameter.getType().toString());
                result.write(' ');
            }
            result.write(parameter.getName());
        }
        result.write(')');
        if (isDeclaration) {
            if (exceptions.length > 0) {
                result.write(" throws ");
                for (int i = 0; i < exceptions.length; i++) {
                    if (i > 0) result.write(", ");
                    result.write(exceptions[i].getValue());
                }
            }
        }
        if (isPrettyPrint) {
            result.write(';');
            result.newline();
        }
    }

    /**
     * @since 1.3
     */
    private String getSignature(boolean withModifiers, boolean isDeclaration) {
        IndentBuffer result = new IndentBuffer();
        writeBody(result, withModifiers, isDeclaration, false);
        return result.toString();
    }

    /**
     * @since 1.3
     */
    public String getDeclarationSignature(boolean withModifiers) {
        return getSignature(withModifiers, true);
    }

    /**
     * @since 1.3
     */
    public String getCallSignature() {
        return getSignature(false, false);
    }

    public void setReturns(Type returns) {
        this.returns = returns;
    }

    public void setParameters(JavaParameter[] parameters) {
        for (int i = 0; i < parameters.length; i++) {
            parameters[i].setParentMethod(this);
        }
        this.parameters = parameters;
    }

    public void setExceptions(Type[] exceptions) {
        this.exceptions = exceptions;
    }

    public void setConstructor(boolean constructor) {
        this.constructor = constructor;
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        JavaMethod m = (JavaMethod) obj;
        if (!m.getName().equals(getName())) return false;
        if (!m.getReturns().equals(getReturns())) return false;

        JavaParameter[] myParams = getParameters();
        JavaParameter[] otherParams = m.getParameters();
        if (otherParams.length != myParams.length) return false;
        for (int i = 0; i < myParams.length; i++) {
            if (!otherParams[i].equals(myParams[i])) return false;
        }

        return true;
    }

    /**
     * @param name method name
     * @param parameterTypes parameter types or null if there are no parameters.
     * @return true if the signature and parameters match.
     */
    public boolean signatureMatches(String name, Type[] parameterTypes) {
        if (!name.equals(this.name)) return false;
        parameterTypes = parameterTypes == null ? new Type[0] : parameterTypes;
        if (parameterTypes.length != this.parameters.length) return false;
        for (int i = 0; i < parameters.length; i++) {
            if (!parameters[i].getType().equals(parameterTypes[i])) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        return name.hashCode() * returns.hashCode() * getParameters().length;
    }

    public JavaClass getParentClass() {
        return parentClass;
    }

    public void setParentClass(JavaClass parentClass) {
        this.parentClass = parentClass;
    }
}

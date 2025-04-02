package dev.qilletni.impl.lang.types;

import dev.qilletni.api.lang.table.Scope;
import dev.qilletni.api.lang.types.ImportAliasType;
import dev.qilletni.api.lang.types.QilletniType;
import dev.qilletni.api.lang.types.typeclass.QilletniTypeClass;
import dev.qilletni.impl.lang.exceptions.UnsupportedOperatorException;

public class ImportAliasTypeImpl implements ImportAliasType {
    
    private final String aliasName;
    private final Scope scope;

    public ImportAliasTypeImpl(String aliasName, Scope scope) {
        this.aliasName = aliasName;
        this.scope = scope;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public String stringValue() {
        return String.format("alias(%s)", aliasName);
    }

    @Override
    public QilletniType plusOperator(QilletniType qilletniType) {
        throw new UnsupportedOperatorException(this, qilletniType, "+");
    }

    @Override
    public void plusOperatorInPlace(QilletniType qilletniType) {
        throw new UnsupportedOperatorException(this, qilletniType, "+");
    }

    @Override
    public QilletniType minusOperator(QilletniType qilletniType) {
        throw new UnsupportedOperatorException(this, qilletniType, "-");
    }

    @Override
    public void minusOperatorInPlace(QilletniType qilletniType) {
        throw new UnsupportedOperatorException(this, qilletniType, "-");
    }

    @Override
    public QilletniTypeClass<?> getTypeClass() {
        return QilletniTypeClass.IMPORT_ALIAS;
    }
}

package is.yarr.qilletni.api.lang.types;

import is.yarr.qilletni.api.lang.table.Scope;

/**
 * A Qilletni type representing a named import alias, with the following syntax <code>import 'xx.ql' as someAlias</code>.
 * This puts the import behind a scope accessed by a name.
 */
public non-sealed interface ImportAliasType extends QilletniType {

    /**
     * The {@link Scope} of the imported file.
     * 
     * @return The scope of the imported file
     */
    Scope getScope();
    
}

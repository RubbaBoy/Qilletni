package is.yarr.qilletni.lang.docs.structure.text;

import java.util.List;

public record DocDescription(List<DescriptionItem> descriptionItems) {

    /**
     * A piece of a doc description. Multiple of these are parsed and pieced together to form the generated
     * documentation.
     */
    public sealed interface DescriptionItem permits DocText, JavaRef, ParamRef {}
    
    public record DocText(String text) implements DescriptionItem {}
    
    public record ParamRef(String paramName) implements DescriptionItem {}
    
    public record JavaRef(String javaName) implements DescriptionItem {}
    
}

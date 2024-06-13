package is.yarr.qilletni.lib;

import com.google.gson.Gson;
import is.yarr.qilletni.api.lib.Library;
import is.yarr.qilletni.api.lib.qll.QllInfo;
import is.yarr.qilletni.lang.exceptions.lib.QllInfoNotFoundException;

import java.io.IOException;
import java.io.UncheckedIOException;

public class QilletniInfoReader {

    private static final Gson gson = new Gson();
    
    public static QllInfo getQllInfo(Library library) {
        try (var qllInfoStream = library.readPath("qll.info").orElseThrow(() -> new QllInfoNotFoundException("Couldn't find qll.info for library " + library.getClass().getCanonicalName()))) {
            return gson.fromJson(new String(qllInfoStream.readAllBytes()), QllInfo.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
}

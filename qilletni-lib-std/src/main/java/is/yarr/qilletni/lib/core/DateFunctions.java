package is.yarr.qilletni.lib.core;

import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.api.lang.types.JavaType;
import is.yarr.qilletni.api.lang.types.entity.EntityInitializer;
import is.yarr.qilletni.api.lib.annotations.NativeOn;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class DateFunctions {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    
    private final EntityInitializer entityInitializer;

    public DateFunctions(EntityInitializer entityInitializer) {
        this.entityInitializer = entityInitializer;
    }

    public EntityType newDateFrom(String dateString) {
        var parsedDate = LocalDate.parse(dateString, formatter);
        
        return entityInitializer.initializeEntity("Date", parsedDate);
    }

    public EntityType newDateNow() {
        var parsedDate = LocalDate.now();
        
        return entityInitializer.initializeEntity("Date", parsedDate);
    }
    
    @NativeOn("Date")
    public int getDay(EntityType entityType) {
        var localDate = entityType.getEntityScope().<JavaType>lookup("_date").getValue().getReference(LocalDate.class);
        return localDate.getDayOfMonth();
    }

    @NativeOn("Date")
    public String getDayName(EntityType entityType) {
        var localDate = entityType.getEntityScope().<JavaType>lookup("_date").getValue().getReference(LocalDate.class);
        return localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
    }

    @NativeOn("Date")
    public int getMonth(EntityType entityType) {
        var localDate = entityType.getEntityScope().<JavaType>lookup("_date").getValue().getReference(LocalDate.class);
        return localDate.getMonthValue();
    }

    @NativeOn("Date")
    public String getMonthName(EntityType entityType) {
        var localDate = entityType.getEntityScope().<JavaType>lookup("_date").getValue().getReference(LocalDate.class);
        return localDate.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
    }

    @NativeOn("Date")
    public int getYear(EntityType entityType) {
        var localDate = entityType.getEntityScope().<JavaType>lookup("_date").getValue().getReference(LocalDate.class);
        return localDate.getYear();
    }

}

package fr.lirmm.aren.ws.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import fr.lirmm.aren.service.AAFImportService;
import fr.lirmm.aren.ws.ObjectMapperProvider;
import javax.ws.rs.core.MediaType;

/**
 * JAX-RS resource class for AAF import
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@RequestScoped
@Path("aaf")
public class AafRESTFacade {

    @Inject
    private AAFImportService aafService;

    @Context
    private ServletContext servletContext;

    @Context
    private ObjectMapperProvider mapperProvider;

    @Context
    private Sse sse;

    /**
     * Import datas from an AAF export file (FicAlimMENESR) Create Institutions,
     * Teams and Users
     *
     * @param uploadedInputStream
     * @param fileDetail
     * @param eventSink
     */
    @POST
    @Path("import")
    @RolesAllowed({"ADMIN"})
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void importAaf(
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @Context SseEventSink eventSink) {

        String realPath = servletContext.getRealPath("");
        String uploadedFileLocation = realPath + "/WEB-INF/AafImport/" + fileDetail.getFileName();
        writeToFile(uploadedInputStream, uploadedFileLocation);

        aafService.setDispatcher((Float progress) -> {
            eventSink.send(sse.newEvent(progress + ""));
        });
        File file = new File(uploadedFileLocation);
        List<String> output = aafService.proceedImportation(file);
        //eventSink.send(sse.newEvent(parseMessage(output)));
        eventSink.send(sse.newEvent("1"));
        eventSink.close();
        file.delete();
    }

    private String parseMessage(List<String> arrayOfString) {

        ObjectMapper mapper = mapperProvider.getContext(null);

        String json = "";
        try {
            json = mapper.writeValueAsString(arrayOfString);
        } catch (JsonProcessingException ex) {
            System.err.println("Message impossible to parse");
        }
        return json;
    }

    private void writeToFile(InputStream uploadedInputStream,
            String uploadedFileLocation) {

        try {
            OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
            int read = 0;
            byte[] bytes = new byte[1024];

            out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

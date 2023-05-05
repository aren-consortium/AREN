/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.lirmm.aren.servlet;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import fr.lirmm.aren.service.CommentService;

/**
 *
 * @author florent
 */
@WebListener
public class BackgroundJobManager implements ServletContextListener {

    @Inject
    private CommentService commentService;

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        OffsetDateTime now = OffsetDateTime.now( ZoneOffset.UTC );
        ZonedDateTime nextMidnight = now.toLocalDate().plusDays( 1 ).atStartOfDay( ZoneOffset.UTC );
        Duration untilMidnight = Duration.between(now, nextMidnight);
        
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.scheduler.scheduleAtFixedRate(() -> {
            try {
              this.commentService.updateAllTags();
            } catch (Exception e) {
              e.printStackTrace();
              // Exception trap to avoid the executor to halt silently
            }
        }, untilMidnight.toMillis(), TimeUnit.DAYS.toMillis(1), TimeUnit.MILLISECONDS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        this.scheduler.shutdownNow();
    }
}

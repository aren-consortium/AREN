/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.lirmm.aren.servlet;

import fr.lirmm.aren.producer.EntityManagerProducer;
import fr.lirmm.aren.service.CommentService;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 *
 * @author florent
 */
@WebListener
public class BackgroundJobManager implements ServletContextListener {

    @Inject
    private EntityManagerProducer emp;

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long untilMidnight = (c.getTimeInMillis() - System.currentTimeMillis());
        long millisInDay = 24 * 60 * 60 * 1000;
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            EntityManager em = emp.createEntityManager();
            CommentService commentService = new CommentService(em);
            commentService.updateAllTags();
            em.close();
        }, untilMidnight, millisInDay, TimeUnit.MILLISECONDS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        scheduler.shutdownNow();
    }
}

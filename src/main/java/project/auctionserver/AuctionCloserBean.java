/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.auctionserver;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

import project.dal.UnitOfWork;
import project.domain.Auction;

/**
 *
 * @author Shalom
 */
@Startup
@Singleton
public class AuctionCloserBean {

    private static final long HOUR = 60 * 60 * 1000L;

    @Resource
    private TimerService timerService;
    private Timer timer;

    public AuctionCloserBean() {
    }

    @PostConstruct
    public void init() {
        TimerConfig config = new TimerConfig();
        config.setPersistent(false);

        LocalDateTime nextHour = LocalDateTime.now().plusHours(1);
        nextHour = LocalDateTime.of(nextHour.getYear(), nextHour.getMonth(), nextHour.getDayOfMonth(), nextHour.getHour(), 1);

        long initialInterval = LocalDateTime.now().until(nextHour, ChronoUnit.MILLIS);

        this.timer = this.timerService.createIntervalTimer(initialInterval, HOUR, config);
    }

    @Timeout
    private synchronized void onTimer(Timer timer) {

        try {

            try ( UnitOfWork unitOfWork = UnitOfWork.create()) {
                List<Auction> auctions = unitOfWork.getClosableAuctions(LocalDateTime.now());

                for (Auction a : auctions) {
                    if (a.canClose()) {
                        a.close();
                    }
                }

                unitOfWork.saveChanges();
            }
        } catch (Exception x) {
            Logger.getLogger(UnitOfWork.class.getName()).log(Level.SEVERE, null, x);
        }
    }
}

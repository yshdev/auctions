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

    private static final long HOUR_IN_MILLIS = 60L * 60L * 1000L;

    @Resource
    private TimerService timerService;
    private Timer timer;

    public AuctionCloserBean() {
    }

    @PostConstruct
    public void init() {
        TimerConfig config = new TimerConfig();
        config.setPersistent(false);

        LocalDateTime nextCheck = LocalDateTime.now().plusHours(1);
        nextCheck = LocalDateTime.of(nextCheck.getYear(), nextCheck.getMonth(), nextCheck.getDayOfMonth(), nextCheck.getHour(), 1);

        long initialInterval = LocalDateTime.now().until(nextCheck, ChronoUnit.MILLIS);

        this.timer = this.timerService.createIntervalTimer(initialInterval, HOUR_IN_MILLIS, config);
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

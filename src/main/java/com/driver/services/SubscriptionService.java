package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());

        //find the total amount
        Integer totalAmount = 0;
        if(subscriptionEntryDto.getSubscriptionType()==SubscriptionType.BASIC)
        {
            totalAmount = 500 + 200 * subscriptionEntryDto.getNoOfScreensRequired();
        } else if (subscriptionEntryDto.getSubscriptionType()==SubscriptionType.PRO) {
            totalAmount = 800 + 250 * subscriptionEntryDto.getNoOfScreensRequired();
        }
        else if(subscriptionEntryDto.getSubscriptionType()==SubscriptionType.ELITE){
            totalAmount = 1000 + 350 * subscriptionEntryDto.getNoOfScreensRequired();
        }

        //set the total amount
        subscription.setTotalAmountPaid(totalAmount);
        subscription.setUser(user);
        user.setSubscription(subscription);
        userRepository.save(user);
        return totalAmount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        User user = userRepository.findById(userId).get();
        Subscription subscription = user.getSubscription();
        SubscriptionType subscriptionType = subscription.getSubscriptionType();
        int diffAmt = 0;
        if(subscriptionType == SubscriptionType.BASIC)
        {
            subscription.setSubscriptionType(SubscriptionType.PRO);

            //find the total cost of upgraded subscription
            Integer total = 800 + 250 * subscription.getNoOfScreensSubscribed();

            //find the difference between previous subscription amount and new subsciption amount
            diffAmt = total - subscription.getTotalAmountPaid();

            subscription.setTotalAmountPaid(total);
            subscription.setStartSubscriptionDate(new Date());

            subscriptionRepository.save(subscription);
            return diffAmt;
        }
        else if(subscriptionType == SubscriptionType.PRO)
        {
            subscription.setSubscriptionType(SubscriptionType.ELITE);

            //find the total cost of upgraded subscription
            Integer total = 1000 + 350 * subscription.getNoOfScreensSubscribed();

            //find the difference between previous subscription amount and new subsciption amount
            diffAmt = total - subscription.getTotalAmountPaid();

            subscription.setTotalAmountPaid(total);
            subscription.setStartSubscriptionDate(new Date());

            subscriptionRepository.save(subscription);
            return diffAmt;
        }
        else {
            throw new Exception("Already the best Subscription");
        }
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        Integer totalRevenue = 0;
        List<Subscription> subscriptions = subscriptionRepository.findAll();

        for(Subscription subscription : subscriptions)
        {
            totalRevenue = totalRevenue + subscription.getTotalAmountPaid();
        }

        return totalRevenue;
    }

}

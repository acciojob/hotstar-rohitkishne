package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        User saveUser = userRepository.save(user);
        Integer userId = saveUser.getId();
        return userId;
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository
        Integer noOfWeb = 0;

        List<WebSeries> webSeriesList= webSeriesRepository.findAll();
        User user = userRepository.findById(userId).get();
        int userAge = user.getAge();
        SubscriptionType userSubscription = user.getSubscription().getSubscriptionType();
        if(userSubscription==SubscriptionType.ELITE)
        {
            noOfWeb = 0;
            for(WebSeries webSeries : webSeriesList)
            {
                int ageLimit = webSeries.getAgeLimit();
                if(userAge>=ageLimit)
                {
                    noOfWeb++;
                }
            }
            return noOfWeb;
        }
        else if(userSubscription == SubscriptionType.PRO)
        {
            noOfWeb = 0;
            for(WebSeries webSeries :webSeriesList)
            {
                int ageLimit = webSeries.getAgeLimit();
                if(webSeries.getSubscriptionType()!=SubscriptionType.ELITE && userAge>=ageLimit)
                {
                    noOfWeb++;
                }
            }
            return noOfWeb;
        }

        noOfWeb = 0;

        for(WebSeries webSeries :webSeriesList)
        {
            int ageLimit = webSeries.getAgeLimit();
            if(webSeries.getSubscriptionType()==SubscriptionType.BASIC && userAge>=ageLimit)
            {
                noOfWeb++;
            }
        }
        return noOfWeb;
    }


}

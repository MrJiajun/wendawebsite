package com.nwecoder.service;

import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2017/2/18 0018.
 */
@Service
public class WendaService {
    public String getMessage(int userId)
    {
        return "Hello Message:"+String.valueOf(userId);
    }
}

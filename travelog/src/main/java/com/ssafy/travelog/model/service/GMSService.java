package com.ssafy.travelog.model.service;

import java.util.List;

public interface GMSService {
    String sendMessage(List<String> userHistories, List<String> userThemes);
}

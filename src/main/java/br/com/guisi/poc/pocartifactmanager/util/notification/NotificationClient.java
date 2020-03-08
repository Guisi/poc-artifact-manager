package br.com.guisi.poc.pocartifactmanager.util.notification;

import java.util.Map;

public interface NotificationClient {

	String publishMessage(String topic, String message, Map<String, String> attributes);
}
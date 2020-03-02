package br.com.guisi.poc.pocartifactmanager.util.notification;

public interface NotificationClient {

	String publishMessage(String topic, String message);
}
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.streaming.responses.GetServerUrlResponse;
import com.vk.api.sdk.streaming.clients.StreamingEventHandler;
import com.vk.api.sdk.streaming.clients.VkStreamingApiClient;
import com.vk.api.sdk.streaming.clients.actors.StreamingActor;
import com.vk.api.sdk.streaming.exceptions.StreamingApiException;
import com.vk.api.sdk.streaming.exceptions.StreamingClientException;
import com.vk.api.sdk.streaming.objects.StreamingCallbackMessage;
import com.vk.api.sdk.streaming.objects.responses.StreamingGetRulesResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class App {
    public static void main(String[] args) {

        TransportClient transportClient = new HttpTransportClient();
        VkStreamingApiClient streamingClient = new VkStreamingApiClient(transportClient);
        VkApiClient vkClient = new VkApiClient(transportClient);
        ConfigReader configReader = new ConfigReader();
        Integer appId = Integer.valueOf(configReader.getProp("appId"));
        String accessToken = configReader.getProp("accessToken");
        ServiceActor serviceActor = new ServiceActor(appId, accessToken);
        GetServerUrlResponse getServerUrlResponse = null;

        try {
            getServerUrlResponse = vkClient.streaming().getServerUrl(serviceActor).execute();
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }

        StreamingActor streamingActor = new StreamingActor(getServerUrlResponse.getEndpoint(), getServerUrlResponse.getKey());
        StreamingGetRulesResponse response = null;
        try {
            response = streamingClient.rules().get(streamingActor).execute();
        } catch (StreamingClientException | StreamingApiException e) {
            e.printStackTrace();
        }
        System.out.println(response);

        WebsoketPrinter websoketPrinter = new WebsoketPrinter();
        websoketPrinter.print();


    }
}

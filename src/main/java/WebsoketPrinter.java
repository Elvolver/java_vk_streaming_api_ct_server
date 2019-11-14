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
import com.vk.api.sdk.streaming.objects.StreamingCallbackMessage;

import java.util.concurrent.ExecutionException;

public class WebsoketPrinter {

    private VkStreamingApiClient streamingClient;
    private StreamingActor streamingActor;
    private VkApiClient vkClient;

    WebsoketPrinter() {
        TransportClient transportClient = new HttpTransportClient();
        streamingClient = new VkStreamingApiClient(transportClient);
        vkClient = new VkApiClient(transportClient);
        streamingActor = new StreamingActor(this.getServerUrlResponse().getEndpoint(), this.getServerUrlResponse().getKey());
    }

    private GetServerUrlResponse getServerUrlResponse() {
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
        return getServerUrlResponse;
    }

    public void print() {
        try {
            streamingClient.stream().get(streamingActor, new StreamingEventHandler() {
                @Override
                public void handle(StreamingCallbackMessage message) {
                    System.out.println(message);
                }
            }).execute();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

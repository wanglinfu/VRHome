package in.srain.cube.request.sender;

import java.net.HttpURLConnection;

import in.srain.cube.request.IRequest;

public class GetRequestSender extends BaseRequestSender {

    public GetRequestSender(IRequest<?> request, HttpURLConnection httpURLConnection) {
        super(request, httpURLConnection);
    }

    @Override
    public void send() {

    }
}

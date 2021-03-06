/**
 * Copyright 2010 - 2015 Monits
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.monits.volleyrequests.network.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.AuthFailureError;

import com.google.gson.Gson;

import org.junit.Test;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

@RunWith(RobolectricTestRunner.class)
public class GsonRequestTest
		extends AbstractJsonRfcCompliantListenableRequestTest<SampleData, GsonRequest<SampleData>> {
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String CHARSET = "UTF-8";
	private static final String STRING_DATA = "my string data";

	@Override
	protected GsonRequest<SampleData> newRequest(final int method, final Response.Listener<SampleData> listener) {
		return new GsonRequest<>(method, "http://www.google.com/", new Gson(), SampleData.class,
				listener, null, null);
	}

	@Override
	protected GsonRequest<SampleData> newRequest(final int method,
				final Response.Listener<SampleData> listener,
				final ListenableRequest.CancelListener cancelListener) {
		return new GsonRequest<>(method, "http://www.google.com/", new Gson(), SampleData.class,
				listener, null, cancelListener, "{}");
	}

	protected SampleData newValidResponse() {
		return new SampleData(STRING_DATA);
	}

	@Test
	public void testParseNetworkResponse() {
		// This method is not delegated, but overridden by the decorator
		final SampleData data = newValidResponse();
		final String json = new Gson().toJson(data);
		final Map<String, String> headers = new HashMap<>();
		headers.put(CONTENT_TYPE, "application/javascript; charset=" + CHARSET);

		try {
			final NetworkResponse networkResponse = new NetworkResponse(
					json.getBytes(CHARSET), headers);
			final Response<SampleData> response = request.parseNetworkResponse(networkResponse);

			assertEquals(data, response.result);
		} catch (final UnsupportedEncodingException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testParseNetworkResponseWithBadEncoding() {
		// This method is not delegated, but overridden by the decorator
		final SampleData data = newValidResponse();
		final String json = new Gson().toJson(data);
		final Map<String, String> headers = new HashMap<>();
		headers.put(CONTENT_TYPE, "application/javascript; charset=nonexistingcharset");

		try {
			final NetworkResponse networkResponse = new NetworkResponse(
					json.getBytes(CHARSET), headers);
			final Response<SampleData> response = request.parseNetworkResponse(networkResponse);

			assertFalse(response.isSuccess());
		} catch (final UnsupportedEncodingException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetNotNullBody() throws AuthFailureError {
		final byte[] body = request.getBody();
		assertNotNull(body);
	}

	@Test
	public void testParseNetworkResponseWithBadJson() {
		// This method is not delegated, but overridden by the decorator
		final String json = "{data: null"; // Malformed json
		final Map<String, String> headers = new HashMap<>();
		headers.put(CONTENT_TYPE, "application/javascript; charset=" + CHARSET);

		try {
			final NetworkResponse networkResponse = new NetworkResponse(
					json.getBytes(CHARSET), headers);
			final Response<SampleData> response = request.parseNetworkResponse(networkResponse);

			assertFalse(response.isSuccess());
		} catch (final UnsupportedEncodingException e) {
			fail(e.getMessage());
		}
	}
}

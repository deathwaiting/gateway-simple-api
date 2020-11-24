package com.m.ahmedgalaltask;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.m.ahmedgalaltask.controllers.dao.GatewayDao;
import com.m.ahmedgalaltask.controllers.dao.PeripheralDeviceDao;
import com.m.ahmedgalaltask.enums.DeviceStatus;
import com.m.ahmedgalaltask.presistence.entities.Gateway;
import com.m.ahmedgalaltask.presistence.entities.PeripheralDevices;
import com.m.ahmedgalaltask.presistence.repositories.GatewayRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.jdbc.Sql;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.IntStream;

import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest(webEnvironment= RANDOM_PORT)
@Sql(value = "/sql/getGatewaysTest.sql", executionPhase = BEFORE_TEST_METHOD)
@Sql(value = "/sql/clear.sql", executionPhase = AFTER_TEST_METHOD)
class AhmedGalalTaskApplicationTests {

	@Autowired
	private TestRestTemplate template;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private GatewayRepository gatewayRepo;

	@Test
	void contextLoads() {
	}


	@Test
	public void getGatewaysTest() throws JsonProcessingException {
		var response = template.getForEntity("/gateway", String.class);

		assertEquals(OK, response.getStatusCode());

		var gateWays = mapper.readValue(response.getBody(), new TypeReference<List<GatewayDao>>() {
		});

		assertEquals(3, gateWays.size());

		var expectedSerials = Set.of("A1", "A2", "A3");
		var expectedDevicesUid = Set.of("U1", "U2", "U3");
		boolean expectedSerialsExists = isExpectedSerialsExists(gateWays, expectedSerials);
		boolean expectedDevicesUIDExists = isExpectedDevicesUIDExists(gateWays, expectedDevicesUid);
		assertTrue(expectedSerialsExists);
		assertTrue(expectedDevicesUIDExists);
	}




	@Test
	public void getSingleGatewayTest() throws JsonProcessingException {
		String serial = "A1";
		var response = template.getForEntity("/gateway/" + serial, GatewayDao.class);

		assertEquals(OK, response.getStatusCode());

		var gateway = response.getBody();
		var devices = gateway.getDevices();

		assertEquals("portal", gateway.getName());
		assertEquals("168.123.159.1", gateway.getIp4Address());
		assertEquals(1, devices.size());
		assertEquals("U1", devices.get(0).getUid());
	}



	@Test
	public void postGatewayTest() throws JSONException {
		var serial = "S123";
		var name = "best gateway ever!";
		var addr = "123.96.78.1";
		var uid = "dev";
		var vendor = "potato";
		var deviceJson = createDeviceJson(uid, vendor);
		var body = createGatewayJson(serial, name, addr, List.of(deviceJson));

		var before = gatewayRepo.findById(serial);
		assertFalse(before.isPresent());

		var response = template.postForEntity("/gateway/", getHttpEntity(body.toString()), Void.class);
		assertEquals(OK, response.getStatusCode());

		assertGatewayDataSaved(serial, name, addr, uid, vendor);
	}




	@Test
	public void updateGatewayTest() throws JSONException {
		var serial = "A1";
		var name = "best gateway ever!";
		var addr = "123.96.78.1";
		var uid = "dev";
		var vendor = "potato";
		var deviceJson = createDeviceJson(uid, vendor);
		var body = createGatewayJson(serial, name, addr, List.of(deviceJson));

		assertGatewayOriginalDataIsDifferent(serial, name, addr, uid, vendor);

		var response = template.exchange("/gateway/", PUT, getHttpEntity(body.toString()), Void.class);
		assertEquals(OK, response.getStatusCode());

		assertGatewayDataSaved(serial, name, addr, uid, vendor);
	}




	@Test
	public void postGatewayWithMoreThan10DevicesTest() throws JSONException {
		var serial = "S123";
		var name = "best gateway ever!";
		var addr = "123.96.78.1";
		var uid = "dev";
		var vendor = "potato";
		var tooManyDevices = createSomeDevices(vendor, 12);
		var body = createGatewayJson(serial, name, addr, tooManyDevices);

		var before = gatewayRepo.findById(serial);
		assertFalse(before.isPresent());

		var response = template.postForEntity("/gateway/", getHttpEntity(body.toString()), Void.class);
		assertEquals(NOT_ACCEPTABLE, response.getStatusCode());

		var after = gatewayRepo.findById(serial);
		assertFalse(after.isPresent());
	}




	@Test
	public void postGatewayWithInvalidIpTest() throws JSONException {
		var serial = "S123";
		var name = "best gateway ever!";
		var addr = "123.INVALID.78.1";
		var uid = "dev";
		var vendor = "potato";
		var deviceJson = createDeviceJson(uid, vendor);
		var body = createGatewayJson(serial, name, addr, List.of(deviceJson));

		var before = gatewayRepo.findById(serial);
		assertFalse(before.isPresent());

		var response = template.postForEntity("/gateway/", getHttpEntity(body.toString()), Void.class);
		assertEquals(NOT_ACCEPTABLE, response.getStatusCode());

		var after = gatewayRepo.findById(serial);
		assertFalse(after.isPresent());
	}



	@Test
	public void postGatewayWithMissingInfoTest() throws JSONException {
		var serial = "S123";
		String name = null;
		var addr = "123.INVALID.78.1";
		var uid = "dev";
		var vendor = "potato";
		var deviceJson = createDeviceJson(uid, vendor);
		var body = createGatewayJson(serial, name, addr, List.of(deviceJson));

		var before = gatewayRepo.findById(serial);
		assertFalse(before.isPresent());

		var response = template.postForEntity("/gateway/", getHttpEntity(body.toString()), Void.class);
		assertEquals(NOT_ACCEPTABLE, response.getStatusCode());

		var after = gatewayRepo.findById(serial);
		assertFalse(after.isPresent());
	}




	@Test
	public void updateGatewayWithNonExistingSerialTest() throws JSONException {
		var serial = "NON-EXISTING";
		var name = "best gateway ever!";
		var addr = "123.96.78.1";
		var uid = "dev";
		var vendor = "potato";
		var deviceJson = createDeviceJson(uid, vendor);
		var body = createGatewayJson(serial, name, addr, List.of(deviceJson));

		var response = template.exchange("/gateway/", PUT, getHttpEntity(body.toString()), Void.class);
		assertEquals(NOT_FOUND, response.getStatusCode());
	}



	@Test
	public void getSingleGatewayNonExistingTest() throws JsonProcessingException {
		String serial = "NON_EXISTING";
		var response = template.getForEntity("/gateway/" + serial, String.class);

		assertEquals(NOT_FOUND, response.getStatusCode());
	}



	@Test
	public void addDeviceToGateWayTest() throws JsonProcessingException {
		var serial = "A1";
		var newDeviceUid = "devNew";

		var gatewayBefore = gatewayRepo.findById(serial);
		assertTrue(gatewayBefore.isPresent());
		var newDeviceExists = isDeviceExists(newDeviceUid, gatewayBefore);
		assertFalse(newDeviceExists);

		var gatewayDao = template.getForEntity("/gateway/" + serial, GatewayDao.class).getBody();
		var newDevice = new PeripheralDeviceDao("devNew", "Merlin", ZonedDateTime.now(), DeviceStatus.ONLINE);
		gatewayDao.getDevices().add(newDevice);

		var json = mapper.writeValueAsString(gatewayDao);
		var response = template.exchange("/gateway/", PUT, getHttpEntity(json), Void.class);

		var gatewayAfter = gatewayRepo.findById(serial);
		assertTrue(gatewayAfter.isPresent());
		var newDeviceExistsAfter = isDeviceExists(newDeviceUid, gatewayAfter);
		assertTrue(newDeviceExistsAfter);
	}



	@Test
	public void removeDeviceToGateWayTest() throws JsonProcessingException {
		var serial = "A1";
		var deviceToRemove = "U1";

		var gatewayBefore = gatewayRepo.findById(serial);
		assertTrue(gatewayBefore.isPresent());
		var deviceExists = isDeviceExists(deviceToRemove, gatewayBefore);
		assertTrue(deviceExists);

		var gatewayDao = template.getForEntity("/gateway/" + serial, GatewayDao.class).getBody();
		gatewayDao.getDevices().removeIf(dev -> Objects.equals(dev.getUid(), deviceToRemove));

		var json = mapper.writeValueAsString(gatewayDao);
		var response = template.exchange("/gateway/", PUT, getHttpEntity(json), Void.class);

		var gatewayAfter = gatewayRepo.findById(serial);
		assertTrue(gatewayAfter.isPresent());
		var deviceExistsAfter = isDeviceExists(deviceToRemove, gatewayAfter);
		assertEquals(0, gatewayAfter.get().getDevices().size());
	}




	private boolean isDeviceExists(String deviceUid, Optional<Gateway> gateway) {
		return gateway
				.stream()
				.map(Gateway::getDevices)
				.flatMap(Set::stream)
				.map(PeripheralDevices::getUid)
				.anyMatch(uid -> Objects.equals(uid, deviceUid));
	}


	private List<JSONObject> createSomeDevices(String vendor, int num) {
		return IntStream
				.range(0, num)
				.parallel()
				.mapToObj(i -> createDeviceJson(randomUUID().toString(), vendor))
				.collect(toList());
	}


	private void assertGatewayDataSaved(String serial, String name, String addr, String uid, String vendor) {
		var after = gatewayRepo.findById(serial);
		assertTrue(after.isPresent());

		var gateway = after.get();
		var device = gateway.getDevices().stream().findFirst().get();
		assertEquals(name, gateway.getName());
		assertEquals(addr, gateway.getIp4Address());
		assertEquals(uid, device.getUid());
		assertEquals(vendor, device.getVendor());
	}




	private void assertGatewayOriginalDataIsDifferent(String serial, String name, String addr, String uid, String vendor) {
		var before = gatewayRepo.findById(serial);
		assertTrue(before.isPresent());

		var gatewayBefore = before.get();
		var deviceBefore = gatewayBefore.getDevices().stream().findFirst().get();
		assertNotEquals(gatewayBefore.getName(), name);
		assertNotEquals(gatewayBefore.getIp4Address(), addr);
		assertNotEquals(deviceBefore.getUid(), uid);
		assertNotEquals(deviceBefore.getVendor(), vendor);
	}


	private JSONObject createGatewayJson(String serial, String name, String addr, List<JSONObject> deviceJsonList) throws JSONException {
		var devicesArray = new JSONArray();
		deviceJsonList.forEach(devicesArray::put);
		return new JSONObject()
				.put("serialNumber", serial)
				.put("name", name)
				.put("ip4Address", addr)
				.put("devices", devicesArray);
	}




	private JSONObject createDeviceJson(String uid, String vendor) {
		try {
			return new JSONObject()
					.put("uid", uid)
					.put("vendor", vendor)
					.put("status", DeviceStatus.ONLINE.name());
		} catch (JSONException e) {
			e.printStackTrace();
			return new JSONObject();
		}
	}



	private boolean isExpectedSerialsExists(List<GatewayDao> gateWays, Set<String> expectedSerials) {
		return gateWays
				.stream()
				.map(GatewayDao::getSerialNumber)
				.allMatch(expectedSerials::contains);
	}



	private boolean isExpectedDevicesUIDExists(List<GatewayDao> gateWays, Set<String> expectedDevicesUid) {
		return gateWays
				.stream()
				.map(GatewayDao::getDevices)
				.flatMap(List::stream)
				.map(PeripheralDeviceDao::getUid)
				.allMatch(expectedDevicesUid::contains);
	}




	private HttpEntity<String> getHttpEntity(String body){
		var headers = new HttpHeaders();
		headers.add("content-type", APPLICATION_JSON_VALUE);
		return new HttpEntity<>(body, headers);
	}

}

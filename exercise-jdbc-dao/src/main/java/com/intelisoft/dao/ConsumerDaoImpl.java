package com.intelisoft.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.intelisoft.api.dao.IConsumerDao;
import com.intelisoft.model.Car;
import com.intelisoft.model.Consumer;

public class ConsumerDaoImpl extends GenericDaoImpl<Consumer> implements IConsumerDao {

	final String CONSUMER_GET_BY_ID_WITH_CAR = "SELECT * FROM consumer LEFT OUTER JOIN car ON car.consumer_id = consumer.id WHERE consumer.id = (?)";

	public ConsumerDaoImpl() {
		super.CREATE = "INSERT INTO consumer (firstName, lastName, birthDate, country, city) VALUES ((?), (?), (?), (?), (?))";
		super.GET_BY_ID = "SELECT * FROM consumer WHERE id = (?)";
		super.GET_ALL = "SELECT * FROM consumer";
		super.UPDATE = "UPDATE consumer SET firstName = (?), lastName = (?), birthDate = (?), country = (?), city =(?) WHERE id = (?)";
		super.DELETE = "DELETE FROM consumer WHERE id = (?)";
	}

	@Override
	public Consumer getByIdWithCars(long id, Connection connection) throws SQLException {

		try (PreparedStatement ps = connection.prepareStatement(CONSUMER_GET_BY_ID_WITH_CAR)) {

			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();

			Consumer consumer = toConsumerWithCars(rs);

			return consumer;
		}
	}

	Consumer toConsumerWithCars(ResultSet rs) throws SQLException {

		rs.next();
		Consumer consumer = toModel(rs);
		if (rs.getString("maker") != null) {
			List<Car> carList = new ArrayList<>();
			do {
				Car car = new CarDaoImpl().toModel(rs);
				carList.add(car);
			} while (rs.next());
			consumer.setCarList(carList);
		}

		return consumer;
	}

	@Override
	Consumer toModel(ResultSet rs) throws SQLException {

		Consumer consumer = new Consumer();

		consumer.setId(rs.getLong("consumer.id"));
		consumer.setFirstName(rs.getString("firstName"));
		consumer.setLastName(rs.getString("lastName"));
		consumer.setBirthDate(rs.getDate("birthDate"));
		consumer.setCountry(rs.getString("country"));
		consumer.setCity(rs.getString("city"));

		return consumer;
	}

	@Override
	PreparedStatement fillPStatement(PreparedStatement ps, Consumer model, boolean isUpdate) throws SQLException {

		ps.setString(1, model.getFirstName());
		ps.setString(2, model.getLastName());
		ps.setDate(3, model.getBirthDate());
		ps.setString(4, model.getCountry());
		ps.setString(5, model.getCity());

		if (isUpdate) {
			ps.setLong(6, model.getId());
		}

		return ps;
	}

	@Override
	List<Consumer> toList(ResultSet rs) throws SQLException {

		List<Consumer> consumerList = new ArrayList<Consumer>();
		while (rs.next()) {
			Consumer consumer = toModel(rs);
			consumerList.add(consumer);
		}

		return consumerList;
	}
}
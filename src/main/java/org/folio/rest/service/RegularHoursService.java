package org.folio.rest.service;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.ext.sql.SQLConnection;

import org.folio.rest.beans.RegularHours;

public interface RegularHoursService {

  Future<Void> saveRegularHours(AsyncResult<SQLConnection> conn, RegularHours entity);

  Future<Void> updateRegularHours(AsyncResult<SQLConnection> conn, RegularHours regularHours);

  Future<List<RegularHours>> findRegularHoursByOpeningId(AsyncResult<SQLConnection> conn, String openingId);

  Future<Void> deleteRegularHoursByOpeningsId(AsyncResult<SQLConnection> conn, String openingsId);
}

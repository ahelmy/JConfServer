///**
// * 
// */
//package outsidethebox.java.backend.dao;
//
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Field;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.ResultSetMetaData;
//import java.sql.SQLException;
//import java.sql.Types;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//import java.util.function.Consumer;
//
//import javax.persistence.Column;
//
//import org.apache.commons.dbcp2.BasicDataSource;
//
//import com.fasterxml.jackson.databind.annotation.JsonAppend.Prop;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//
//import outsidethebox.java.hibernate.models.DBConnection;
//import outsidethebox.java.hibernate.models.Model;
//import outsidethebox.java.hibernate.models.Project;
//import outsidethebox.java.hibernate.models.Property;
//import outsidethebox.java.utils.ResourceUtils;
//
///**
// * @author AliHelmy
// *
// */
//public class DBUtils {
//	private static BasicDataSource dataSource;
//
//	private static BasicDataSource getDataSource() {
//		if (dataSource == null) {
//			BasicDataSource ds = new BasicDataSource();
//			String db = "db/Configuration.db";
//			String fullPath = ResourceUtils.getFile(db).getAbsolutePath().replaceAll("%20", " ") + "";
//			ds.setUrl("jdbc:sqlite:" + fullPath);
//			System.out.println(fullPath);
//			ds.setUsername("");
//			ds.setPassword("");
//			ds.setDriverClassName("org.sqlite.JDBC");
//
//			ds.setMinIdle(5);
//			ds.setMaxIdle(10);
//			ds.setMaxOpenPreparedStatements(100);
//
//			dataSource = ds;
//		}
//		return dataSource;
//	}
//
//	public static Connection getConnection() throws SQLException {
//		return getDataSource().getConnection();
//	}
//
//	private static final String SAVE_PROPERTY_QUERY = "INSERT INTO Property(ID, Name, Value, Description, ProjID, ConID) VALUES (?, ?, ?, ?, ?, ?)";
//	private static final String UPDATE_PROPERTY_BY_ID_QUERY = "UPDATE Property  SET Name = ?, Value = ? , Description = ?, ProjID = ?, ConID = ? WHERE ID = ?";
//	private static final String UPDATE_PROPERTY_BY_NAME_QUERY = "UPDATE Property  SET Name = ?, Value = ? , Description = ?, ProjID = ?, ConID = ? WHERE NAME = ?";
//
//	private static final String DELETE_PROPERTY_BY_ID_QUERY = "DELETE FROM Property WHERE ID = ?";
//	private static final String DELETE_PROPERTY_BY_NAME_QUERY = "DELETE FROM Property  WHERE Name = ?";
//
//	private static final String GET_PROPERTY_QUERY = "SELECT ID, Name, Value, Description, ProjID, ConID FROM Property WHERE Name = ?";
//
//	public static Property getProperty(String name) throws SQLException {
//		try (Connection conn = getConnection()) {
//			return getProperty(name, conn);
//		} catch (SQLException ex) {
//			throw ex;
//		}
//	}
//
//	public static Property getProperty(String name, Connection conn) throws SQLException {
//		try (PreparedStatement prepStmt = conn.prepareStatement(GET_PROPERTY_QUERY)) {
//			prepStmt.setString(1, name);
//			try (ResultSet result = prepStmt.executeQuery()) {
//				Property property = null;
//				if (result.next()) {
//					property = new Property();
//					property.setId(result.getString("ID"));
//					property.setName(result.getString("Name"));
//					property.setValue(result.getString("Value"));
//					property.setDescription(result.getString("Description"));
//					property.setProject(new Project(result.getString("ProjID")));
//					String conID = result.getString("ConID");
//					if (conID != null) {
//						property.setDbconnection(new DBConnection(conID));
//					}
//				}
//				return property;
//			} catch (SQLException ex) {
//				throw ex;
//			}
//		} catch (SQLException ex) {
//			throw ex;
//		}
//	}
//
//	private static String capFirst(String str) {
//		return String.valueOf(str.charAt(0)).toUpperCase() + str.substring(1);
//	}
//
//	public static <T> T get(T obj, final String fieldName) throws SQLException {
//		StringBuilder query = new StringBuilder("SELECT ");
//		StringBuilder value = new StringBuilder("");
//		Map<String, Field> fieldToColumn = new HashMap<>();
//		Arrays.asList(obj.getClass().getDeclaredFields()).forEach(field -> {
//			try {
//				Column col = (Column) field.getAnnotation(Column.class);
//				if (col != null) {
//					query.append(col.name()).append(", ");
//					if (col.name().equals(fieldName)) {
//						value.append(String.valueOf(
//								obj.getClass().getDeclaredMethod("get" + capFirst(field.getName())).invoke(obj)));
//					}
//					fieldToColumn.put(col.name(), field);
//				}
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
//		});
//		query.deleteCharAt(query.lastIndexOf(","));// ,query.lastIndexOf(","));//
//													// = query.substring(0,
//													// query.lastIndexOf(",") -
//													// 1);
//		query.append(" FROM ").append(obj.getClass().getSimpleName()).append(" WHERE ").append(fieldName)
//				.append(" = ?");
//		System.out.println(query);
//		try (Connection conn = getConnection(); PreparedStatement prepStmt = conn.prepareStatement(query.toString())) {
//			prepStmt.setString(1, value.toString());
//			try (ResultSet result = prepStmt.executeQuery();) {
//				ResultSetMetaData meta = result.getMetaData();
//				if (result.next()) {
//					for (int i = 1; i <= meta.getColumnCount(); i++) {
//						try {
//							Field f = fieldToColumn.get(meta.getColumnName(i));
//							String methodName = "set" + capFirst(f.getName());
//							Object objVal = result.getObject(meta.getColumnName(i));
//							obj.getClass().getDeclaredMethod(methodName, f.getType()).invoke(obj, objVal);
//						} catch (Exception ex) {
//							ex.printStackTrace();
//						}
//					}
//				} else {
//					return null;
//				}
//				return obj;
//			} catch (SQLException ex) {
//				throw ex;
//			}
//		} catch (SQLException ex) {
//			throw ex;
//		}
//	}
//
//	public static int persistProperty(Property property) throws SQLException {
//		if (property.validPOST()) {
//			final Property prop = getProperty(property.getName());
//			if (prop == null) {
//				return saveProperty(property);
//			} else {
//				return updateProperty(property);
//			}
//		}
//		throw new SQLException("Not a valid Property");
//	}
//
//	public static int persistProperties(List<Property> properties) throws SQLException {
//		List<Integer> updatedCount = new LinkedList<>();
//		try (Connection conn = getConnection();
//				PreparedStatement prepSaveStmt = conn.prepareStatement(SAVE_PROPERTY_QUERY);
//				PreparedStatement prepUpdateByIDStmt = conn.prepareStatement(UPDATE_PROPERTY_BY_ID_QUERY);
//				PreparedStatement prepUpdateByNameStmt = conn.prepareStatement(UPDATE_PROPERTY_BY_NAME_QUERY)) {
//			for (Property property : properties) {
//				if (property.validPOST()) {
//					final Property prop = getProperty(property.getName(), conn);
//					if (prop == null) {
//						int indx = 1;
//						property.setId(generateID());
//						prepSaveStmt.setObject(indx++, property.getId());
//						prepSaveStmt.setObject(indx++, property.getName());
//						prepSaveStmt.setObject(indx++, property.getValue());
//						prepSaveStmt.setObject(indx++, property.getDescription());
//						prepSaveStmt.setObject(indx++, property.getProject().getId());
//						if (property.getDbconnection() != null) {
//							prepSaveStmt.setObject(indx++, property.getDbconnection().getId());
//						} else {
//							prepSaveStmt.setNull(indx++, Types.VARCHAR);
//						}
//						prepSaveStmt.addBatch();
//
//					} else {
//						if (property.getId() != null) {
//							int indx = 1;
//							prepUpdateByIDStmt.setObject(indx++, property.getName());
//							prepUpdateByIDStmt.setObject(indx++, property.getValue());
//							prepUpdateByIDStmt.setObject(indx++, property.getDescription());
//							prepUpdateByIDStmt.setObject(indx++, property.getProject().getId());
//							if (property.getDbconnection() != null) {
//								prepUpdateByIDStmt.setObject(indx++, property.getDbconnection().getId());
//							} else {
//								prepUpdateByIDStmt.setNull(indx++, Types.VARCHAR);
//							}
//							prepUpdateByIDStmt.setObject(indx++, property.getId());
//
//							prepUpdateByIDStmt.addBatch();
//						} else {
//							int indx = 1;
//							prepUpdateByNameStmt.setObject(indx++, property.getName());
//							prepUpdateByNameStmt.setObject(indx++, property.getValue());
//							prepUpdateByNameStmt.setObject(indx++, property.getDescription());
//							prepUpdateByNameStmt.setObject(indx++, property.getProject().getId());
//							if (property.getDbconnection() != null) {
//								prepUpdateByNameStmt.setObject(indx++, property.getDbconnection().getId());
//							} else {
//								prepUpdateByNameStmt.setNull(indx++, Types.VARCHAR);
//							}
//							prepUpdateByNameStmt.setObject(indx++, property.getName());
//
//							prepUpdateByNameStmt.addBatch();
//						}
//					}
//				}
//			}
//			int[] save = prepSaveStmt.executeBatch();
//			int[] updateID = prepUpdateByIDStmt.executeBatch();
//			int[] updateName = prepUpdateByNameStmt.executeBatch();
//			for (int s : save) {
//				if (s != 0) {
//					updatedCount.add(s);
//				}
//			}
//			for (int u : updateID) {
//				if (u != 0) {
//					updatedCount.add(u);
//				}
//			}
//			for (int u : updateName) {
//				if (u != 0) {
//					updatedCount.add(u);
//				}
//			}
//		} catch (SQLException ex) {
//			throw ex;
//		}
//		if (updatedCount.size() != properties.size()) {
//			throw new SQLException("Some properties have not been persisted.");
//		}
//
//		return updatedCount.size();
//	}
//
//	public static int deleteProperty(Property property) throws SQLException {
//		try (Connection conn = getConnection()) {
//			if (property.getId() != null) {
//				try (PreparedStatement prepStmt = conn.prepareStatement(DELETE_PROPERTY_BY_ID_QUERY)) {
//					prepStmt.setObject(1, property.getId());
//					return prepStmt.executeUpdate();
//				} catch (SQLException ex) {
//					throw ex;
//				}
//			} else {
//				try (PreparedStatement prepStmt = conn.prepareStatement(DELETE_PROPERTY_BY_NAME_QUERY)) {
//					prepStmt.setObject(1, property.getName());
//					return prepStmt.executeUpdate();
//				} catch (SQLException ex) {
//					throw ex;
//				}
//			}
//		} catch (SQLException ex) {
//			throw ex;
//		}
//	}
//
//	public static int updateProperty(Property property) throws SQLException {
//		try (Connection conn = getConnection()) {
//			return updateProperty(property, conn);
//		} catch (SQLException ex) {
//			throw ex;
//		}
//	}
//
//	public static int updateProperty(Property property, Connection conn) throws SQLException {
//		if (property.validPOST()) {
//			if (property.getId() != null) {
//				try (PreparedStatement prepStmt = conn.prepareStatement(UPDATE_PROPERTY_BY_ID_QUERY)) {
//					int indx = 1;
//					prepStmt.setObject(indx++, property.getName());
//					prepStmt.setObject(indx++, property.getValue());
//					prepStmt.setObject(indx++, property.getDescription());
//					prepStmt.setObject(indx++, property.getProject().getId());
//					if (property.getDbconnection() != null) {
//						prepStmt.setObject(indx++, property.getDbconnection().getId());
//					} else {
//						prepStmt.setNull(indx++, Types.VARCHAR);
//					}
//
//					prepStmt.setObject(indx++, property.getId());
//					return prepStmt.executeUpdate();
//				} catch (SQLException ex) {
//					throw ex;
//				}
//			} else {
//				try (PreparedStatement prepStmt = conn.prepareStatement(UPDATE_PROPERTY_BY_NAME_QUERY)) {
//					int indx = 1;
//					property.setId(property.getName());
//					prepStmt.setObject(indx++, property.getName());
//					prepStmt.setObject(indx++, property.getValue());
//					prepStmt.setObject(indx++, property.getDescription());
//					prepStmt.setObject(indx++, property.getProject().getId());
//					if (property.getDbconnection() != null) {
//						prepStmt.setObject(indx++, property.getDbconnection().getId());
//					} else {
//						prepStmt.setNull(indx++, Types.VARCHAR);
//					}
//					prepStmt.setObject(indx++, property.getName());
//					return prepStmt.executeUpdate();
//				} catch (SQLException ex) {
//					throw ex;
//				}
//			}
//		}
//		throw new SQLException("Not a valid Property");
//
//	}
//
//	public static int saveProperty(Property property) throws SQLException {
//		try (Connection conn = getConnection()) {
//			return saveProperty(property, conn);
//		} catch (SQLException ex) {
//			throw ex;
//		}
//	}
//
//	public static int saveProperty(Property property, Connection conn) throws SQLException {
//		if (property.validPOST()) {
//			try (PreparedStatement prepStmt = conn.prepareStatement(SAVE_PROPERTY_QUERY)) {
//				int indx = 1;
//				property.setId(generateID());
//				prepStmt.setObject(indx++, property.getId());
//				prepStmt.setObject(indx++, property.getName());
//				prepStmt.setObject(indx++, property.getValue());
//				prepStmt.setObject(indx++, property.getDescription());
//				prepStmt.setObject(indx++, property.getProject().getId());
//				if (property.getDbconnection() != null) {
//					prepStmt.setObject(indx++, property.getDbconnection().getId());
//				} else {
//					prepStmt.setNull(indx++, Types.VARCHAR);
//				}
//				return prepStmt.executeUpdate();
//			} catch (SQLException ex) {
//				throw ex;
//			}
//		}
//		throw new SQLException("Not a valid Property");
//	}
//
//	public static String generateID() {
//		return UUID.randomUUID().toString();
//	}
//
//	public static <T extends Model> int save(T obj) throws SQLException {
//		JsonObject json = obj.toJSON();
//		for (Field field : obj.getClass().getDeclaredFields()) {
//			Class type = field.getType();
//			String name = field.getName();
//			Annotation[] annotations = field.getDeclaredAnnotations();
//			Arrays.asList(annotations).stream().forEach(a -> {
//				System.out.println(a.annotationType());
//			});
//		}
//		try (Connection conn = getConnection()) {
//			List<String> columns = new LinkedList<>();
//			List<String> values = new LinkedList<>();
//			StringBuilder cols = new StringBuilder(), vals = new StringBuilder();
//			for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
//				if (entry.getValue().isJsonPrimitive()) {
//					columns.add(entry.getKey());
//					values.add(entry.getValue().getAsString());
//					cols.append(entry.getKey()).append(",");
//					vals.append("?,");
//				}
//			}
//			cols.deleteCharAt(cols.length() - 1);
//			vals.deleteCharAt(vals.length() - 1);
//			String insertSQL = String.format("", obj.getClass().getSimpleName(), cols.toString(), vals.toString());
//
//			try (PreparedStatement prepStmt = conn.prepareStatement(insertSQL)) {
//				for (int i = 0; i < values.size(); i++) {
//					prepStmt.setObject(i + 1, values.get(i));
//				}
//				return prepStmt.executeUpdate();
//			} catch (SQLException ex) {
//				throw ex;
//			}
//		} catch (SQLException ex) {
//			throw ex;
//		}
//	}
//}

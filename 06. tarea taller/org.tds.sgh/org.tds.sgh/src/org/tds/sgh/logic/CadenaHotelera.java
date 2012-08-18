package org.tds.sgh.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tds.sgh.dto.HotelDTO;
import org.tds.sgh.dto.ReservaDTO;
import org.tds.sgh.dto.TiposHabitacionDTO;
import org.tds.sgh.logic.Precondition.PreconditionException;

public class CadenaHotelera implements IDatosCadenaHotelera,
		IAltaHotelController, IAltaClienteController,
		IIdentificarClienteController, IHacerReservaController,
		ITomarReservaController

{

	private static CadenaHotelera instance;

	public static CadenaHotelera getInstance() {
		if (instance == null)
			instance = new CadenaHotelera();
		return instance;
	}

	private static long numeracionReservas = 0;

	// Atributos --------------------------------------------------------------

	private Map<String, Cliente> clientes;

	private Map<String, Hotel> hoteles;

	private Map<String, TipoHabitacion> tiposHabitacion;

	private Hotel hotelEnUso;

	// Constructores ----------------------------------------------------------
	public CadenaHotelera() {
		this.clientes = new HashMap<String, Cliente>();
		this.hoteles = new HashMap<String, Hotel>();
		this.tiposHabitacion = new HashMap<String, TipoHabitacion>();
	}

	// IDatosCadenaHotelera ---------------------------------------------------

	@Override
	public Map<String, IDatosCliente> listarClientes() {
		return Collections.<String, IDatosCliente> unmodifiableMap(clientes);
	}

	@Override
	public Map<String, IDatosHotel> listarHoteles() {
		return Collections.<String, IDatosHotel> unmodifiableMap(hoteles);
	}

	@Override
	public Map<String, IDatosTipoHabitacion> listarTiposHabitacion() {
		return Collections
				.<String, IDatosTipoHabitacion> unmodifiableMap(tiposHabitacion);
	}

	@Override
	public Map<String, IDatosHabitacion> listarHabitaciones(String nombreHotel) {
		Precondition.contains(hoteles, nombreHotel, "El hotel '" + nombreHotel
				+ "' no est� registrado");
		return hoteles.get(nombreHotel).listarHabitaciones();
	}

	@Override
	public Map<Long, IDatosReserva> listarReservasCliente(String nombreCliente) {
		Map<Long, IDatosReserva> mapDatosReserva = new HashMap<Long, IDatosReserva>();
		Cliente cliente = this.clientes.get(nombreCliente);
		for (Reserva reserva : cliente.getReservas().values()) {
			IDatosReserva iDatosReserva = new ReservaDTO(reserva);
			mapDatosReserva.put(iDatosReserva.getCodigo(), iDatosReserva);
		}

		return mapDatosReserva;
	}

	// MAREL
	@Override
	public Map<Long, IDatosReserva> listarReservasHotel(String nombreHotel) {
		Hotel hotel = this.hoteles.get(nombreHotel);
		Map<Long, IDatosReserva> res = new HashMap<Long, IDatosReserva>();

		// Long, Reserva
		for (Reserva reserva : hotel.listarReservasHotel().values()) {
			res.put(reserva.getCodigo(), reserva);
		}

		return res;
	}

	@Override
	public IDatosTipoHabitacion obtenerTipoHabitacionDeHabitacion(
			String nombreHotel, String nombreHabitacion) {
		Precondition.contains(hoteles, nombreHotel, "El hotel '" + nombreHotel
				+ "' no est� registrado");
		return hoteles.get(nombreHotel).obtenerTipoHabitacionDeHabitacion(
				nombreHabitacion);
	}

	
	/**
	 * Marel Oliva
	 */
	@Override
	public IDatosTipoHabitacion obtenerTipoHabitacionDeReserva(long codigo) {
		IDatosTipoHabitacion  res = new TiposHabitacionDTO(null);
		
		for (Hotel hotel : this.hoteles.values()) {
			for ( Reserva reserva : hotel.listarReservasHotel().values() ) {
				if ( reserva.getCodigo() == codigo ) {
					res = new TiposHabitacionDTO(reserva.getTipoHabitacion());
				}
			}
		}
		return res;
	}

	@Override
	public IDatosHabitacion obtenerHabitacionDeReserva(long codigo) {
		// TODO
		return null;
	}

	// IAltaHotel -------------------------------------------------------------

	@Override
	public IDatosHotel registrarHotel(String nombre) {
		Precondition.notContain(hoteles, nombre,
				"Ya existe un hotel con el nombre '" + nombre + "'");

		Hotel h = new Hotel(nombre);
		hoteles.put(nombre, h);
		return h;
	}

	@Override
	public IDatosTipoHabitacion registrarTipoHabitacion(String nombre) {
		Precondition.notContain(tiposHabitacion, nombre,
				"Ya existe un tipo de habitaci�n con el nombre '" + nombre
						+ "'");
		
		TipoHabitacion tipoHabitacion = new TipoHabitacion(nombre);

		
		tiposHabitacion.put(nombre, tipoHabitacion);
		return tipoHabitacion;
	}

	@Override
	public IDatosHabitacion registrarHabitacion(String nombreHotel,
			String nombreTipoHabitacion, String nombre) {
		Precondition.contains(hoteles, nombreHotel,
				"No existe un hotel con el nombre '" + nombreHotel + "'");
		Precondition.contains(tiposHabitacion, nombreTipoHabitacion,
				"No existe un tipo de habitaci�n con el nombre '"
						+ nombreTipoHabitacion + "'");

		Hotel hotel = hoteles.get(nombreHotel);
		TipoHabitacion tipoHabitacion = tiposHabitacion
				.get(nombreTipoHabitacion);

		return hotel.registrarHabitacion(tipoHabitacion, nombre);
	}

	// IAltaCliente -----------------------------------------------------------

	@Override
	public IDatosCliente registrarCliente(String nombre, String telefono,
			String email) {
		// Precondition.notContain(clientes, nombre,
		// "Ya existe un cliente con el nombre '" + nombre + "'");

		Cliente cliente = new Cliente(nombre, telefono, email);
		clientes.put(nombre, cliente);
		return cliente;
	}

	@Override
	public IDatosCliente seleccionarCliente(String nombre) {
		Cliente c = clientes.get(nombre);
		if (c == null) {
			throw new PreconditionException(
					"No se puede seleccionar un cliente que no existe en el sistema, por favor registre el cliente");
		}
		IDatosCliente dc = c.export();
		return dc;
	}

	// MAREL
	@Override
	public void confirmarReserva(long codigoReserva) {
		hotelEnUso.confirmarReserva(codigoReserva);

	}

	/**
	 * Marel Oliva
	 */
	@Override
	public boolean confirmarDisponibilidad(String nombreHotel,
			String nombreTipoHabitacion, GregorianCalendar fechaInicio,
			GregorianCalendar fechaFin) {

		hotelEnUso = hoteles.get(nombreHotel);
		
		Precondition.isNotNull(hotelEnUso, "No existe el hotel propocionado " + nombreHotel);
		Precondition.isNotNull(tiposHabitacion.get(nombreTipoHabitacion), "No existe el tipo de habitaci�n propocionado " + nombreHotel);
		Precondition.isTrue(fechaInicio.compareTo(fechaFin) < 0, "El rango de fechas propocionados incorrecto");
		
		return hotelEnUso.confirmarDisponibilidad(nombreTipoHabitacion,
				fechaInicio, fechaFin);
	}

	/**
	 * Marel Oliva
	 */
	@Override
	public List<IDatosHotel> sugerirAlternativas(String nombreTipoHabitacion,
			GregorianCalendar fechaInicio, GregorianCalendar fechaFin) {

		
		Precondition.isNotNull(this.tiposHabitacion.get(nombreTipoHabitacion), 
				"El nombre del tipo de habitaci�n es inv�lido " );
		
		Precondition.isTrue(fechaInicio.compareTo(fechaFin) < 0, "El rango de fechas propocionados incorrecto");
		
		List<IDatosHotel> hotelesDTO = new ArrayList<IDatosHotel>();

		for (Hotel hotel : hoteles.values()) {
			if (hotel.confirmarDisponibilidad(nombreTipoHabitacion,
					fechaInicio, fechaFin)) {
				hotelesDTO.add(new HotelDTO(hotel));
			}
		}

		return hotelesDTO;
	}

	/**
	 * Marel Oliva
	 */
	@Override
	public IDatosReserva registrarReserva(String nombreCliente,
			String nombreHotel, String nombreTipoHabitacion,
			GregorianCalendar fechaInicio, GregorianCalendar fechaFin,
			boolean modificablePorHuesped) {

		numeracionReservas += 1;

		Hotel hotel = this.hoteles.get(nombreHotel);
		Cliente cliente = this.clientes.get(nombreCliente);
		TipoHabitacion tipoHabitacion = this.tiposHabitacion
		.get(nombreTipoHabitacion);
		
		Precondition.isNotNull(cliente, "No existe el cliente proporcionado " + nombreCliente);
		Precondition.isNotNull(hotel, "No existe el hotel proporcionado " + nombreHotel);
		Precondition.isNotNull(tipoHabitacion, "No existe el tipo de habitacion" + nombreTipoHabitacion);
		Precondition.isTrue(fechaInicio.compareTo(fechaFin) < 0, "El rango de fechas propocionados incorrecto");
		

		return hotel.registrarReserva(numeracionReservas, cliente,
				tipoHabitacion, fechaInicio, fechaFin, modificablePorHuesped);

	}

	/**
	 * Alvaro Jose Peralta ocampo
	 */
	@Override
	public List<IDatosReserva> buscarReservasPendientes(String nombreCliente) {
		Cliente cliente = clientes.get(nombreCliente);
		return cliente.buscarReservasPendientes();
	}

	/**
	 * Alvaro jose peralta ocampo
	 */
	@Override
	public List<IDatosReserva> buscarReservasNoTomadas(String nombreHotel,
			GregorianCalendar fecha) {
		// busca en la lista de hoteles el hotel
		hotelEnUso = this.hoteles.get(nombreHotel);
		return hotelEnUso.buscarReservasNoTomadas(fecha);
	}

	@Override
	public IDatosReserva seleccionarReserva(long codigoReserva) {
		return hotelEnUso.seleccionarReserva(codigoReserva);
	}

	@Override
	public IDatosHuesped registrarHuesped(long codigoReserva, String nombre,
			String documento) {
		return hotelEnUso.registrarHuespedEnReservaSeleccionada(codigoReserva,
				nombre, documento);
	}

	@Override
	public IDatosHabitacion tomarReserva(long codigoReserva) {
		return hotelEnUso.tomarReserva(codigoReserva);
	}

	/**
	 * Nelson Yanez
	 */
	public List<IDatosCliente> buscarCliente(String nombreRegex) {
		// **restona los datos de cliente que cumplen con contener tring
		// nombreRegex **//
		List<IDatosCliente> clis = new ArrayList<IDatosCliente>();

		for (Cliente cli : this.clientes.values()) {
			if (cli.getNombre().matches(nombreRegex)) {

				clis.add(new DatosCliente(cli.getNombre(), cli.getTelefono(),
						cli.getEMail()));
			}

		}
		return clis;
	}
}

package org.tds.sgh.logic;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.tds.sgh.dto.HabitacionDTO;
import org.tds.sgh.dto.HuespedDTO;

@Entity
public class Reserva {

	private long codigo;
	
	private long id;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	
	public void setCodigo(long codigo) {
		this.codigo = codigo;
	}


	public long getCodigo() {
		return this.codigo;
	}

	private GregorianCalendar fechaInicio;

	private GregorianCalendar fechaFin;
	private boolean isModificablePorHuesped;
	EstadoReserva estadoReserva;
	private TipoHabitacion tipoHabitacion;
	private Habitacion habitacion;
	
	
	// nelson Ya�ez//
	
	
	private List<Huesped> huespedes;

	@OneToMany(cascade=CascadeType.ALL)
	public List<Huesped> getHuespedes() {
		return huespedes;
	}

	public void setHuespedes(List<Huesped> huespedes) {
		this.huespedes = huespedes;
	}
	
	public Reserva(long codigo, GregorianCalendar fechaInicio,
			GregorianCalendar fechaFin, boolean modificablePorHuesped) {
		this.codigo = codigo;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
		this.isModificablePorHuesped = modificablePorHuesped;
		estadoReserva = EstadoReserva.PENDIENTE;
		huespedes = new ArrayList<Huesped>();
	}
	

	public boolean isPendiente() {
		return estadoReserva.compareTo(EstadoReserva.PENDIENTE) == 0;
	}

	public boolean isTomada() {
		return estadoReserva.compareTo(EstadoReserva.TOMADA) == 0;
	}

	public boolean isCancelada() {
		return estadoReserva.compareTo(EstadoReserva.CANCELADA) == 0;
	}
	
	@OneToOne(cascade=CascadeType.ALL)
	public TipoHabitacion getTipoHabitacion() {
		return tipoHabitacion;
	}

	public boolean estasEnFechayNoTomada(GregorianCalendar fecha) {
		boolean estasEnFecha = fechaInicio.compareTo(fecha) <= 0
				&& fechaFin.compareTo(fecha) >= 0;
		return estasEnFecha && !isTomada();
	}

	public void registraTipoHabitacion(TipoHabitacion tipoHabitacion) {
		this.tipoHabitacion = tipoHabitacion;
	}

	public boolean estasEnRango(Reserva reservaSeleccionada) {
		return reservaSeleccionada.fechaInicio.after(this.fechaFin)
				&& reservaSeleccionada.fechaFin.before(this.fechaInicio);
	}

	public boolean esTuHabitacion(Habitacion habitacion) {
		return this.habitacion.equals(habitacion);
	}

	/**
	 * alvaro modificacion para agregar la condicion
	 * 
	 * @param habitacionLibre
	 * @return
	 */
	public IDatosHabitacion tomarReserva(Habitacion habitacionLibre) {
		Precondition.isFalse(isTomada(),
				"No se puede tomar porque la reserva ya esta tomada");
		Precondition.isFalse(isCancelada(),
				"No se puede tomar porque la reserva esta cancelada");
		Precondition
				.isFalse(huespedes.size() <= 0,
						"No se puede tomar porque la reserva porque no tiene huespedes asociados");
		this.habitacion = habitacionLibre;
		this.estadoReserva = EstadoReserva.TOMADA;
		IDatosHabitacion iDatosHabitacion = new HabitacionDTO(habitacionLibre);
		return iDatosHabitacion;
	}

	// neslon ya�ez//
	public IDatosHuesped registarHuesped(String nombre2, String documento) {
		Huesped huesped = new Huesped(nombre2, documento);
		huespedes.add(huesped);
		IDatosHuesped iDatosHuesped = new HuespedDTO(huesped);
		return iDatosHuesped;
	}

	public GregorianCalendar getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(GregorianCalendar fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public GregorianCalendar getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(GregorianCalendar fechaFin) {
		this.fechaFin = fechaFin;
	}

	public boolean isModificablePorHuesped() {
		return isModificablePorHuesped;
	}

	public void setModificablePorHuesped(boolean isModificablePorHuesped) {
		this.isModificablePorHuesped = isModificablePorHuesped;
	}

	public EstadoReserva getEstadoReserva() {
		return estadoReserva;
	}

	public void setEstadoReserva(EstadoReserva estadoReserva) {
		this.estadoReserva = estadoReserva;
	}
	
	@OneToOne(cascade=CascadeType.ALL)
	public Habitacion getHabitacion() {
		return habitacion;
	}

	public void setHabitacion(Habitacion habitacion) {
		this.habitacion = habitacion;
	}

	public void setTipoHabitacion(TipoHabitacion tipoHabitacion) {
		this.tipoHabitacion = tipoHabitacion;
	}
	
	public void setCancelada(boolean cancelada){
		if(cancelada){
			this.estadoReserva = EstadoReserva.CANCELADA;
		}	
	}
	
	public void setPendiente(boolean pendiente){
		if(pendiente){
			this.estadoReserva = EstadoReserva.PENDIENTE;
		}	
	}
	
	public void setTomada(boolean tomada){
		if(tomada){
			this.estadoReserva = EstadoReserva.TOMADA;
		}	
	}
	
	
	

}

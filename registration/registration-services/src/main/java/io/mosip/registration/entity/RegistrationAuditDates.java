package io.github.tf-govstack.registration.entity;

import java.sql.Timestamp;

/**
 * Interface to retrieve the start and end {@link Timestamp} of the audit logs
 * sent along with the packet
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public interface RegistrationAuditDates {

	Timestamp getAuditLogFromDateTime();
	Timestamp getAuditLogToDateTime();

}

function getTags( thisEvent ) {
	if ( document.getElementById( "studentIdentifierKey" ) == null
			|| document.getElementById( "studentIdentifierKey" ) == undefined
			|| document.getElementById( "studentIdentifierKey" ).value.trim() == "" ) {
		alert( "Student identifier key cannot be empty. You can generate these keys using the LTI application, " );
		return false;
	}
	if ( document.getElementById( "instructorIdentifierKey" ) == null
		|| document.getElementById( "instructorIdentifierKey" ) == undefined
		|| document.getElementById( "instructorIdentifierKey" ).value.trim() == "" ) {
		alert( "Instructor identifier key cannot be empty. You can generate these keys using the LTI application, " );
		return false;
	}
	if ( document.getElementById( "version" ) !== null
			&& document.getElementById( "version" ).value.trim().length > 0 ) {
		if ( !document.getElementById( 'integrityHash' )
				|| document.getElementById( 'integrityHash' ).value.trim().length === 0 ) {
			alert( "If the version is set, you should also set the integrity hash!" );
			return false;
		}
	}
	return true;
}
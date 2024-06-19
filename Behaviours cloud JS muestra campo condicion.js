const descriptionValue = getFieldById("customfield_10786").getValue();
const endpointStatusField = getFieldById("customfield_10867");

endpointStatusField.setVisible(false); // Ocultar por defecto

// Verificar si alguno de los objetos en descriptionValue tiene value que contiene "Webcalc"
if (descriptionValue.some(obj => obj.value.includes("Webcalc"))) {
    endpointStatusField.setVisible(true); // Mostrar si alguno cumple la condición
}

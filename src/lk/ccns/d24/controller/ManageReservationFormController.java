/*
 * @author : Supun Jaysinghe
 * Date    : 17 April 2023
 * Time    : 9:56 PM
 * Project : d24_hotel_manage_system
 * Created by IntelliJ IDEA.
 */
package lk.ccns.d24.controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import lk.ccns.d24.bo.BOFactory;
import lk.ccns.d24.bo.Custom.ManageReserveBO;
import lk.ccns.d24.bo.Custom.ManageRoomBO;
import lk.ccns.d24.bo.Custom.ManageStudentBO;
import lk.ccns.d24.dto.ReservationDTO;
import lk.ccns.d24.dto.RoomDTO;
import lk.ccns.d24.dto.StudentDTO;

import java.io.IOException;


public class ManageReservationFormController {
    private final ManageReserveBO manageReserveBO = (ManageReserveBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.MANAGE_RESERVE);
    private final ManageStudentBO manageStudentBO = (ManageStudentBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.MANAGE_STUDENT);
    private final ManageRoomBO manageRoomBO = (ManageRoomBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.MANAGE_ROOM);


    public AnchorPane reservationAP;
    public JFXTextField txtReservationId;
    public JFXComboBox<String> cmbPayStatus;
    public JFXComboBox<String> cmbStudentID;
    public JFXTextField txtName;
    public JFXTextField txtAddress;
    public JFXTextField txtContact;
    public JFXTextField txtDOB;
    public JFXTextField txtGender;
    public JFXComboBox<String> cmbRoomID;
    public JFXTextField txtKeyMoney;
    public JFXTextField txtQTY;
    public JFXTextField txtType;
    public JFXDatePicker dpReserveDate;
    public TableView<ReservationDTO> reservationTableView;
    public TableColumn colId;
    public TableColumn colDate;
    public TableColumn colStudentId;
    public TableColumn colRoomId;
    public TableColumn colStatus;
    public JFXTextField txtRoomQTY;


    public void initialize() {
        setCmbData();
        setCmbStudentIdRoomIDData();
        loadReservationDetailsToTable();

        txtName.setEditable(false);
        txtAddress.setEditable(false);
        txtContact.setEditable(false);
        txtDOB.setEditable(false);
        txtGender.setEditable(false);
        txtType.setEditable(false);
        txtQTY.setEditable(false);
        txtKeyMoney.setEditable(false);

        reservationTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                setReserveDataToField(newValue);
            }
        });

        colId.setCellValueFactory(new PropertyValueFactory<>("reserve_id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colRoomId.setCellValueFactory(new PropertyValueFactory<>("room_id"));
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("Student_id"));


    }

    public void makeReserveOnAction(MouseEvent mouseEvent) {
        try {
            boolean isSave = manageReserveBO.saveReserve(getReservationDTO());
            if (isSave) {
                Boolean isUpdateQTY = manageRoomBO.update(new RoomDTO(
                        cmbRoomID.getValue(),
                        txtType.getText(),
                        Double.parseDouble(txtKeyMoney.getText()),
                        Integer.parseInt(txtQTY.getText()) - Integer.parseInt(txtRoomQTY.getText())
                ));
                cleanTextFieldOnAction(mouseEvent);
                loadReservationDetailsToTable();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        cleanTextFieldOnAction(mouseEvent);
    }

    private void setReserveDataToField(ReservationDTO newValue) {
        txtReservationId.setText(newValue.getReserve_id());
        cmbPayStatus.setValue(newValue.getStatus());
        dpReserveDate.setValue(newValue.getDate());
        cmbStudentID.setValue(newValue.getStudent_id());
        setStudentDetails(newValue.getStudent_id());
        cmbRoomID.setValue(newValue.getRoom_id());
        setRoomDetails(newValue.getRoom_id());
    }

    private void setRoomDetails(String room_id) {
        try {
            RoomDTO room = manageRoomBO.findById(room_id);
            txtKeyMoney.setText(String.valueOf(room.getKey_money()));
            txtQTY.setText(String.valueOf(room.getQty()));
            txtType.setText(room.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setStudentDetails(String student_id) {
        try {
            StudentDTO student = manageStudentBO.findStudent(student_id);
            txtName.setText(student.getName());
            txtAddress.setText(student.getAddress());
            txtContact.setText(student.getContact());
            txtDOB.setText(student.getDob());
            txtGender.setText(student.getGender());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadReservationDetailsToTable() {
        try {
            reservationTableView.setItems(manageReserveBO.getAllDetails());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setCmbStudentIdRoomIDData() {
        try {
            ObservableList<StudentDTO> allStudent = manageStudentBO.getAllStudent();
            ObservableList<RoomDTO> allRoom = manageRoomBO.getAllData();
            ObservableList<String> studentIdList = FXCollections.observableArrayList();
            ObservableList<String> roomIdList = FXCollections.observableArrayList();

            for (StudentDTO studentDTO : allStudent) {
                studentIdList.add(studentDTO.getStudent_id());
            }
            for (RoomDTO roomDTO : allRoom) {
                roomIdList.add(roomDTO.getRoom_id());
            }
            cmbStudentID.setItems(studentIdList);
            cmbRoomID.setItems(roomIdList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setCmbData() {
        cmbPayStatus.getItems().add("Paid");
        cmbPayStatus.getItems().add("Later");
    }

    public void addNewStudentOnAction(MouseEvent mouseEvent) {

    }

    private ReservationDTO getReservationDTO() {
        return new ReservationDTO(
                txtReservationId.getText(),
                cmbStudentID.getValue(),
                cmbRoomID.getValue(),
                cmbPayStatus.getValue()
        );
    }

    public void cleanTextFieldOnAction(MouseEvent mouseEvent) {
        txtReservationId.clear();
        cmbPayStatus.setValue(null);
        dpReserveDate.setValue(null);
        cmbStudentID.setValue(null);
        txtName.clear();
        txtAddress.clear();
        txtContact.clear();
        txtDOB.clear();
        txtGender.clear();
        cmbRoomID.setValue(null);
        txtType.clear();
        txtQTY.clear();
        txtKeyMoney.clear();
        txtRoomQTY.clear();

    }

    public void selectStudentIdOnAction(ActionEvent actionEvent) {

        setStudentDetails(cmbStudentID.getValue());
    }

    public void selectRoomIdOnAction(ActionEvent actionEvent) {
        setRoomDetails(cmbRoomID.getValue());
    }

    public void updateReserveOnAction(MouseEvent mouseEvent) {
        try {
            boolean isUpdate = manageReserveBO.updateReserveData(
                    new ReservationDTO(
                            txtReservationId.getText(),
                            dpReserveDate.getValue(),
                            cmbStudentID.getValue().toString(),
                            cmbRoomID.getValue().toString(),
                            cmbPayStatus.getValue().toString()

                    )
            );
            cleanTextFieldOnAction(mouseEvent);
            loadReservationDetailsToTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteReserveOnAction(MouseEvent mouseEvent) {
        try {
            boolean isDelete = manageReserveBO.deleteReserveData(getReserveDTO());
            cleanTextFieldOnAction(mouseEvent);
            loadReservationDetailsToTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ReservationDTO getReserveDTO() {
        return new ReservationDTO(
                txtReservationId.getText(),
                cmbStudentID.getValue().toString(),
                cmbRoomID.getValue().toString(),
                cmbPayStatus.getValue().toString()
        );
    }
}

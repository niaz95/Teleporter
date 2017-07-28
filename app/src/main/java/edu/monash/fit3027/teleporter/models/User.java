package edu.monash.fit3027.teleporter.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by niaz on 20/4/17.
 */

public class User implements Parcelable {



    private String userId;
    private String FirstName;
    private String LastName;
    private String PhoneNo;
    private String Email;
    private String Password;
    private int isTeleporter;

    public User(String userId, String m_sFirstName, String m_sLastName, String m_sPhoneNo, String m_sEmail, String m_sPassword, int m_sTeleporter){

        this.userId = userId;
        this.FirstName = m_sFirstName;
        this.LastName = m_sLastName;
        this.PhoneNo = m_sPhoneNo;
        this.Email = m_sEmail;
        this.Password = m_sPassword;
        this.isTeleporter = m_sTeleporter;
    }

    public User(){

    }

    protected User(Parcel in){
        //_id = in.readLong();
        userId = in.readString();
        FirstName = in.readString();
        LastName = in.readString();
        PhoneNo = in.readString();
        Email = in.readString();
        Password = in.readString();
        isTeleporter = in.readByte();
    }

    public static final Creator<User> CREATOR = new Creator<User>(){
        @Override
        public User createFromParcel(Parcel in){ return new User(in);}

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //dest.writeLong(_id);
        dest.writeString(userId);
        dest.writeString(FirstName);
        dest.writeString(LastName);
        dest.writeString(PhoneNo);
        dest.writeString(Email);
        dest.writeString(Password);
        dest.writeInt(isTeleporter);

    }

    //Mutator and accessor methods

    public String getUserId() {return userId;}

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public void setPhoneNo(String phoneNo) {
        PhoneNo = phoneNo;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setIsTeleporter(int isTeleporter) {
        this.isTeleporter = isTeleporter;
    }

    public String getFirstName(){return FirstName;}

    public String getLastName(){return LastName;}

    public String getPhoneNo(){return PhoneNo; }

    public String getEmail(){return Email; }

    public String getPassword(){return Password; }

    public int getIsTeleporter(){return isTeleporter;}




}

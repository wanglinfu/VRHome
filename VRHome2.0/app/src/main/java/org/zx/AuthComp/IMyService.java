/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\WorkSpace\\AndroidSpace\\Proj_VRHome\\Project\\VRHome_ZTE\\src\\org\\zx\\AuthComp\\IMyService.aidl
 */
package org.zx.AuthComp;
public interface IMyService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements IMyService
{
private static final String DESCRIPTOR = "org.zx.AuthComp.IMyService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an org.zx.AuthComp.IMyService interface,
 * generating a proxy if needed.
 */
public static IMyService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof IMyService))) {
return ((IMyService)iin);
}
return new Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_startAccountManagerActivity:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _result = this.startAccountManagerActivity();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_startAddAccountActivity:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _result = this.startAddAccountActivity();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_startRegisterActivity:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _result = this.startRegisterActivity();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_startGetPwdActivity:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _result = this.startGetPwdActivity();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_startModifyInfoActivity:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _result = this.startModifyInfoActivity();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_startModifyPwdActivity:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _result = this.startModifyPwdActivity();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_deleteToken:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.deleteToken();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getToken:
{
data.enforceInterface(DESCRIPTOR);
String _result = this.getToken();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getUser:
{
data.enforceInterface(DESCRIPTOR);
String _result = this.getUser();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_loginByToken:
{
data.enforceInterface(DESCRIPTOR);
String _arg0;
_arg0 = data.readString();
String _result = this.loginByToken(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_createBaiduDiskAccount:
{
data.enforceInterface(DESCRIPTOR);
String _arg0;
_arg0 = data.readString();
String _arg1;
_arg1 = data.readString();
String _arg2;
_arg2 = data.readString();
int _result = this.createBaiduDiskAccount(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_updateBaiduDiskAccount:
{
data.enforceInterface(DESCRIPTOR);
String _arg0;
_arg0 = data.readString();
String _arg1;
_arg1 = data.readString();
String _arg2;
_arg2 = data.readString();
int _result = this.updateBaiduDiskAccount(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_requestVerifyCodeImage:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
android.graphics.Bitmap _result = this.requestVerifyCodeImage(_arg0, _arg1);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_setServerAddr:
{
data.enforceInterface(DESCRIPTOR);
String _arg0;
_arg0 = data.readString();
this.setServerAddr(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_requestToken:
{
data.enforceInterface(DESCRIPTOR);
String _arg0;
_arg0 = data.readString();
String _arg1;
_arg1 = data.readString();
String _arg2;
_arg2 = data.readString();
String _result = this.requestToken(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_registerUserInfo:
{
data.enforceInterface(DESCRIPTOR);
String _arg0;
_arg0 = data.readString();
String _arg1;
_arg1 = data.readString();
String _arg2;
_arg2 = data.readString();
String _arg3;
_arg3 = data.readString();
String _arg4;
_arg4 = data.readString();
int _arg5;
_arg5 = data.readInt();
String _arg6;
_arg6 = data.readString();
int _result = this.registerUserInfo(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_modifyUserPwd:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
String _arg1;
_arg1 = data.readString();
String _arg2;
_arg2 = data.readString();
int _result = this.modifyUserPwd(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getUserPwdByMobile:
{
data.enforceInterface(DESCRIPTOR);
String _arg0;
_arg0 = data.readString();
int _result = this.getUserPwdByMobile(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getUserPwdByEmail:
{
data.enforceInterface(DESCRIPTOR);
String _arg0;
_arg0 = data.readString();
int _result = this.getUserPwdByEmail(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_modifyUserInfo:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
String _arg1;
_arg1 = data.readString();
String _arg2;
_arg2 = data.readString();
String _arg3;
_arg3 = data.readString();
String _arg4;
_arg4 = data.readString();
int _arg5;
_arg5 = data.readInt();
int _result = this.modifyUserInfo(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_checkUserEmail:
{
data.enforceInterface(DESCRIPTOR);
String _arg0;
_arg0 = data.readString();
int _result = this.checkUserEmail(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_checkUserMobile:
{
data.enforceInterface(DESCRIPTOR);
String _arg0;
_arg0 = data.readString();
int _result = this.checkUserMobile(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_loginByUserInfo:
{
data.enforceInterface(DESCRIPTOR);
String _arg0;
_arg0 = data.readString();
String _arg1;
_arg1 = data.readString();
String _arg2;
_arg2 = data.readString();
int _result = this.loginByUserInfo(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_addSnsAccount:
{
data.enforceInterface(DESCRIPTOR);
String _arg0;
_arg0 = data.readString();
boolean _arg1;
_arg1 = (0!=data.readInt());
String _arg2;
_arg2 = data.readString();
int _result = this.addSnsAccount(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_startRegisterMainActivity:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _result = this.startRegisterMainActivity();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_startLoginActivity:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _result = this.startLoginActivity();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getUserImage:
{
data.enforceInterface(DESCRIPTOR);
android.graphics.Bitmap _result = this.getUserImage();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_startCreditPayActivity:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _result = this.startCreditPayActivity();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_startJPayActivity:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _result = this.startJPayActivity();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getJPayRecordInfo:
{
data.enforceInterface(DESCRIPTOR);
String _arg0;
_arg0 = data.readString();
String _arg1;
_arg1 = data.readString();
String _arg2;
_arg2 = data.readString();
String _arg3;
_arg3 = data.readString();
String _arg4;
_arg4 = data.readString();
String _result = this.getJPayRecordInfo(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getJPayInfo:
{
data.enforceInterface(DESCRIPTOR);
String _arg0;
_arg0 = data.readString();
String _arg1;
_arg1 = data.readString();
String _arg2;
_arg2 = data.readString();
String _arg3;
_arg3 = data.readString();
String _arg4;
_arg4 = data.readString();
String _arg5;
_arg5 = data.readString();
String _result = this.getJPayInfo(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_silentRegister:
{
data.enforceInterface(DESCRIPTOR);
String _arg0;
_arg0 = data.readString();
String _arg1;
_arg1 = data.readString();
String _arg2;
_arg2 = data.readString();
String _arg3;
_arg3 = data.readString();
String _result = this.silentRegister(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_silentFastRegister:
{
data.enforceInterface(DESCRIPTOR);
String _arg0;
_arg0 = data.readString();
String _arg1;
_arg1 = data.readString();
String _result = this.silentFastRegister(_arg0, _arg1);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_silentLogin:
{
data.enforceInterface(DESCRIPTOR);
String _arg0;
_arg0 = data.readString();
String _arg1;
_arg1 = data.readString();
String _arg2;
_arg2 = data.readString();
String _result = this.silentLogin(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_startAddAccountActivityWangqin:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _result = this.startAddAccountActivityWangqin();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_credit:
{
data.enforceInterface(DESCRIPTOR);
String _arg0;
_arg0 = data.readString();
String _arg1;
_arg1 = data.readString();
String _arg2;
_arg2 = data.readString();
int _arg3;
_arg3 = data.readInt();
String _arg4;
_arg4 = data.readString();
String _result = this.credit(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_startRegisterVipActivity:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _result = this.startRegisterVipActivity();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_startPayment:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _result = this.startPayment();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_queryPayLog:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
String _arg1;
_arg1 = data.readString();
String _arg2;
_arg2 = data.readString();
String _arg3;
_arg3 = data.readString();
String _arg4;
_arg4 = data.readString();
String[] _result = this.queryPayLog(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
reply.writeStringArray(_result);
return true;
}
case TRANSACTION_startAddMobileActivity:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _result = this.startAddMobileActivity();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_silentRegister2:
{
data.enforceInterface(DESCRIPTOR);
String _arg0;
_arg0 = data.readString();
String _arg1;
_arg1 = data.readString();
String _arg2;
_arg2 = data.readString();
String _arg3;
_arg3 = data.readString();
String _arg4;
_arg4 = data.readString();
String _arg5;
_arg5 = data.readString();
String _result = this.silentRegister2(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_silentFastRegister2:
{
data.enforceInterface(DESCRIPTOR);
String _arg0;
_arg0 = data.readString();
String _arg1;
_arg1 = data.readString();
String _arg2;
_arg2 = data.readString();
String _arg3;
_arg3 = data.readString();
String _result = this.silentFastRegister2(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_silentLogin2:
{
data.enforceInterface(DESCRIPTOR);
String _arg0;
_arg0 = data.readString();
String _arg1;
_arg1 = data.readString();
String _arg2;
_arg2 = data.readString();
String _arg3;
_arg3 = data.readString();
String _arg4;
_arg4 = data.readString();
String _result = this.silentLogin2(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_startSnsBindMobileActivity:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _result = this.startSnsBindMobileActivity();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getTokenByApp:
{
data.enforceInterface(DESCRIPTOR);
String _arg0;
_arg0 = data.readString();
String _arg1;
_arg1 = data.readString();
String _arg2;
_arg2 = data.readString();
String _arg3;
_arg3 = data.readString();
String _result = this.getTokenByApp(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getUserImage2:
{
data.enforceInterface(DESCRIPTOR);
android.graphics.Bitmap _result = this.getUserImage2();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements IMyService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public android.os.Bundle startAccountManagerActivity() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startAccountManagerActivity, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.os.Bundle startAddAccountActivity() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startAddAccountActivity, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.os.Bundle startRegisterActivity() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startRegisterActivity, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.os.Bundle startGetPwdActivity() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startGetPwdActivity, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.os.Bundle startModifyInfoActivity() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startModifyInfoActivity, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.os.Bundle startModifyPwdActivity() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startModifyPwdActivity, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int deleteToken() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_deleteToken, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public String getToken() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getToken, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public String getUser() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getUser, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public String loginByToken(String token) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(token);
mRemote.transact(Stub.TRANSACTION_loginByToken, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int createBaiduDiskAccount(String token, String access_token, String expire) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(token);
_data.writeString(access_token);
_data.writeString(expire);
mRemote.transact(Stub.TRANSACTION_createBaiduDiskAccount, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int updateBaiduDiskAccount(String token, String access_token, String expire) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(token);
_data.writeString(access_token);
_data.writeString(expire);
mRemote.transact(Stub.TRANSACTION_updateBaiduDiskAccount, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.graphics.Bitmap requestVerifyCodeImage(int w, int h) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.graphics.Bitmap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(w);
_data.writeInt(h);
mRemote.transact(Stub.TRANSACTION_requestVerifyCodeImage, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.graphics.Bitmap.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setServerAddr(String url) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(url);
mRemote.transact(Stub.TRANSACTION_setServerAddr, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public String requestToken(String email, String mobile, String pwd) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(email);
_data.writeString(mobile);
_data.writeString(pwd);
mRemote.transact(Stub.TRANSACTION_requestToken, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int registerUserInfo(String email, String mobile, String pwd, String nickname, String des, int group, String invoker) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(email);
_data.writeString(mobile);
_data.writeString(pwd);
_data.writeString(nickname);
_data.writeString(des);
_data.writeInt(group);
_data.writeString(invoker);
mRemote.transact(Stub.TRANSACTION_registerUserInfo, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int modifyUserPwd(int uid, String curpwd, String newpwd) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
_data.writeString(curpwd);
_data.writeString(newpwd);
mRemote.transact(Stub.TRANSACTION_modifyUserPwd, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getUserPwdByMobile(String mobile) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(mobile);
mRemote.transact(Stub.TRANSACTION_getUserPwdByMobile, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getUserPwdByEmail(String email) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(email);
mRemote.transact(Stub.TRANSACTION_getUserPwdByEmail, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int modifyUserInfo(int uid, String email, String mobile, String nickname, String ext, int group) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
_data.writeString(email);
_data.writeString(mobile);
_data.writeString(nickname);
_data.writeString(ext);
_data.writeInt(group);
mRemote.transact(Stub.TRANSACTION_modifyUserInfo, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int checkUserEmail(String email) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(email);
mRemote.transact(Stub.TRANSACTION_checkUserEmail, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int checkUserMobile(String mobile) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(mobile);
mRemote.transact(Stub.TRANSACTION_checkUserMobile, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int loginByUserInfo(String email, String mobile, String password) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(email);
_data.writeString(mobile);
_data.writeString(password);
mRemote.transact(Stub.TRANSACTION_loginByUserInfo, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int addSnsAccount(String json, boolean bAllowBind, String invoker) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(json);
_data.writeInt(((bAllowBind)?(1):(0)));
_data.writeString(invoker);
mRemote.transact(Stub.TRANSACTION_addSnsAccount, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.os.Bundle startRegisterMainActivity() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startRegisterMainActivity, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.os.Bundle startLoginActivity() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startLoginActivity, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.graphics.Bitmap getUserImage() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.graphics.Bitmap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getUserImage, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.graphics.Bitmap.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.os.Bundle startCreditPayActivity() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startCreditPayActivity, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.os.Bundle startJPayActivity() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startJPayActivity, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public String getJPayRecordInfo(String appid, String uid, String goodsid, String starttime, String endtime) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(appid);
_data.writeString(uid);
_data.writeString(goodsid);
_data.writeString(starttime);
_data.writeString(endtime);
mRemote.transact(Stub.TRANSACTION_getJPayRecordInfo, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public String getJPayInfo(String appid, String uid, String goodsid, String starttime, String endtime, String state) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(appid);
_data.writeString(uid);
_data.writeString(goodsid);
_data.writeString(starttime);
_data.writeString(endtime);
_data.writeString(state);
mRemote.transact(Stub.TRANSACTION_getJPayInfo, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public String silentRegister(String email, String phone, String password, String appid) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(email);
_data.writeString(phone);
_data.writeString(password);
_data.writeString(appid);
mRemote.transact(Stub.TRANSACTION_silentRegister, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public String silentFastRegister(String phone, String appid) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(phone);
_data.writeString(appid);
mRemote.transact(Stub.TRANSACTION_silentFastRegister, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public String silentLogin(String email, String phone, String password) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(email);
_data.writeString(phone);
_data.writeString(password);
mRemote.transact(Stub.TRANSACTION_silentLogin, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.os.Bundle startAddAccountActivityWangqin() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startAddAccountActivityWangqin, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public String credit(String cmd, String token, String appid, int ruleid, String sign) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(cmd);
_data.writeString(token);
_data.writeString(appid);
_data.writeInt(ruleid);
_data.writeString(sign);
mRemote.transact(Stub.TRANSACTION_credit, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.os.Bundle startRegisterVipActivity() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startRegisterVipActivity, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.os.Bundle startPayment() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startPayment, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public String[] queryPayLog(int uid, String goodsid, String appid, String orderid, String sign) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
String[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
_data.writeString(goodsid);
_data.writeString(appid);
_data.writeString(orderid);
_data.writeString(sign);
mRemote.transact(Stub.TRANSACTION_queryPayLog, _data, _reply, 0);
_reply.readException();
_result = _reply.createStringArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.os.Bundle startAddMobileActivity() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startAddMobileActivity, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public String silentRegister2(String email, String phone, String password, String appid, String model, String imei) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(email);
_data.writeString(phone);
_data.writeString(password);
_data.writeString(appid);
_data.writeString(model);
_data.writeString(imei);
mRemote.transact(Stub.TRANSACTION_silentRegister2, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public String silentFastRegister2(String phone, String appid, String model, String imei) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(phone);
_data.writeString(appid);
_data.writeString(model);
_data.writeString(imei);
mRemote.transact(Stub.TRANSACTION_silentFastRegister2, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public String silentLogin2(String email, String phone, String password, String model, String imei) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(email);
_data.writeString(phone);
_data.writeString(password);
_data.writeString(model);
_data.writeString(imei);
mRemote.transact(Stub.TRANSACTION_silentLogin2, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.os.Bundle startSnsBindMobileActivity() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startSnsBindMobileActivity, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public String getTokenByApp(String appid, String nonce, String timestamp, String sign) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(appid);
_data.writeString(nonce);
_data.writeString(timestamp);
_data.writeString(sign);
mRemote.transact(Stub.TRANSACTION_getTokenByApp, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.graphics.Bitmap getUserImage2() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.graphics.Bitmap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getUserImage2, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.graphics.Bitmap.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_startAccountManagerActivity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_startAddAccountActivity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_startRegisterActivity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_startGetPwdActivity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_startModifyInfoActivity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_startModifyPwdActivity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_deleteToken = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_getToken = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_getUser = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_loginByToken = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_createBaiduDiskAccount = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_updateBaiduDiskAccount = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_requestVerifyCodeImage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_setServerAddr = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_requestToken = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_registerUserInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
static final int TRANSACTION_modifyUserPwd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
static final int TRANSACTION_getUserPwdByMobile = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
static final int TRANSACTION_getUserPwdByEmail = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
static final int TRANSACTION_modifyUserInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
static final int TRANSACTION_checkUserEmail = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
static final int TRANSACTION_checkUserMobile = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
static final int TRANSACTION_loginByUserInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 22);
static final int TRANSACTION_addSnsAccount = (android.os.IBinder.FIRST_CALL_TRANSACTION + 23);
static final int TRANSACTION_startRegisterMainActivity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 24);
static final int TRANSACTION_startLoginActivity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 25);
static final int TRANSACTION_getUserImage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 26);
static final int TRANSACTION_startCreditPayActivity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 27);
static final int TRANSACTION_startJPayActivity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 28);
static final int TRANSACTION_getJPayRecordInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 29);
static final int TRANSACTION_getJPayInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 30);
static final int TRANSACTION_silentRegister = (android.os.IBinder.FIRST_CALL_TRANSACTION + 31);
static final int TRANSACTION_silentFastRegister = (android.os.IBinder.FIRST_CALL_TRANSACTION + 32);
static final int TRANSACTION_silentLogin = (android.os.IBinder.FIRST_CALL_TRANSACTION + 33);
static final int TRANSACTION_startAddAccountActivityWangqin = (android.os.IBinder.FIRST_CALL_TRANSACTION + 34);
static final int TRANSACTION_credit = (android.os.IBinder.FIRST_CALL_TRANSACTION + 35);
static final int TRANSACTION_startRegisterVipActivity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 36);
static final int TRANSACTION_startPayment = (android.os.IBinder.FIRST_CALL_TRANSACTION + 37);
static final int TRANSACTION_queryPayLog = (android.os.IBinder.FIRST_CALL_TRANSACTION + 38);
static final int TRANSACTION_startAddMobileActivity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 39);
static final int TRANSACTION_silentRegister2 = (android.os.IBinder.FIRST_CALL_TRANSACTION + 40);
static final int TRANSACTION_silentFastRegister2 = (android.os.IBinder.FIRST_CALL_TRANSACTION + 41);
static final int TRANSACTION_silentLogin2 = (android.os.IBinder.FIRST_CALL_TRANSACTION + 42);
static final int TRANSACTION_startSnsBindMobileActivity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 43);
static final int TRANSACTION_getTokenByApp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 44);
static final int TRANSACTION_getUserImage2 = (android.os.IBinder.FIRST_CALL_TRANSACTION + 45);
}
public android.os.Bundle startAccountManagerActivity() throws android.os.RemoteException;
public android.os.Bundle startAddAccountActivity() throws android.os.RemoteException;
public android.os.Bundle startRegisterActivity() throws android.os.RemoteException;
public android.os.Bundle startGetPwdActivity() throws android.os.RemoteException;
public android.os.Bundle startModifyInfoActivity() throws android.os.RemoteException;
public android.os.Bundle startModifyPwdActivity() throws android.os.RemoteException;
public int deleteToken() throws android.os.RemoteException;
public String getToken() throws android.os.RemoteException;
public String getUser() throws android.os.RemoteException;
public String loginByToken(String token) throws android.os.RemoteException;
public int createBaiduDiskAccount(String token, String access_token, String expire) throws android.os.RemoteException;
public int updateBaiduDiskAccount(String token, String access_token, String expire) throws android.os.RemoteException;
public android.graphics.Bitmap requestVerifyCodeImage(int w, int h) throws android.os.RemoteException;
public void setServerAddr(String url) throws android.os.RemoteException;
public String requestToken(String email, String mobile, String pwd) throws android.os.RemoteException;
public int registerUserInfo(String email, String mobile, String pwd, String nickname, String des, int group, String invoker) throws android.os.RemoteException;
public int modifyUserPwd(int uid, String curpwd, String newpwd) throws android.os.RemoteException;
public int getUserPwdByMobile(String mobile) throws android.os.RemoteException;
public int getUserPwdByEmail(String email) throws android.os.RemoteException;
public int modifyUserInfo(int uid, String email, String mobile, String nickname, String ext, int group) throws android.os.RemoteException;
public int checkUserEmail(String email) throws android.os.RemoteException;
public int checkUserMobile(String mobile) throws android.os.RemoteException;
public int loginByUserInfo(String email, String mobile, String password) throws android.os.RemoteException;
public int addSnsAccount(String json, boolean bAllowBind, String invoker) throws android.os.RemoteException;
public android.os.Bundle startRegisterMainActivity() throws android.os.RemoteException;
public android.os.Bundle startLoginActivity() throws android.os.RemoteException;
public android.graphics.Bitmap getUserImage() throws android.os.RemoteException;
public android.os.Bundle startCreditPayActivity() throws android.os.RemoteException;
public android.os.Bundle startJPayActivity() throws android.os.RemoteException;
public String getJPayRecordInfo(String appid, String uid, String goodsid, String starttime, String endtime) throws android.os.RemoteException;
public String getJPayInfo(String appid, String uid, String goodsid, String starttime, String endtime, String state) throws android.os.RemoteException;
public String silentRegister(String email, String phone, String password, String appid) throws android.os.RemoteException;
public String silentFastRegister(String phone, String appid) throws android.os.RemoteException;
public String silentLogin(String email, String phone, String password) throws android.os.RemoteException;
public android.os.Bundle startAddAccountActivityWangqin() throws android.os.RemoteException;
public String credit(String cmd, String token, String appid, int ruleid, String sign) throws android.os.RemoteException;
public android.os.Bundle startRegisterVipActivity() throws android.os.RemoteException;
public android.os.Bundle startPayment() throws android.os.RemoteException;
public String[] queryPayLog(int uid, String goodsid, String appid, String orderid, String sign) throws android.os.RemoteException;
public android.os.Bundle startAddMobileActivity() throws android.os.RemoteException;
public String silentRegister2(String email, String phone, String password, String appid, String model, String imei) throws android.os.RemoteException;
public String silentFastRegister2(String phone, String appid, String model, String imei) throws android.os.RemoteException;
public String silentLogin2(String email, String phone, String password, String model, String imei) throws android.os.RemoteException;
public android.os.Bundle startSnsBindMobileActivity() throws android.os.RemoteException;
public String getTokenByApp(String appid, String nonce, String timestamp, String sign) throws android.os.RemoteException;
public android.graphics.Bitmap getUserImage2() throws android.os.RemoteException;
}

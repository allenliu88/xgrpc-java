// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: xgrpc_grpc_service.proto

package io.xgrpc.api.grpc.auto;

public interface MetadataOrBuilder extends
    // @@protoc_insertion_point(interface_extends:Metadata)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string type = 3;</code>
   */
  String getType();
  /**
   * <code>string type = 3;</code>
   */
  com.google.protobuf.ByteString
      getTypeBytes();

  /**
   * <code>string clientIp = 8;</code>
   */
  String getClientIp();
  /**
   * <code>string clientIp = 8;</code>
   */
  com.google.protobuf.ByteString
      getClientIpBytes();

  /**
   * <code>map&lt;string, string&gt; headers = 7;</code>
   */
  int getHeadersCount();
  /**
   * <code>map&lt;string, string&gt; headers = 7;</code>
   */
  boolean containsHeaders(
      String key);
  /**
   * Use {@link #getHeadersMap()} instead.
   */
  @Deprecated
  java.util.Map<String, String>
  getHeaders();
  /**
   * <code>map&lt;string, string&gt; headers = 7;</code>
   */
  java.util.Map<String, String>
  getHeadersMap();
  /**
   * <code>map&lt;string, string&gt; headers = 7;</code>
   */

  String getHeadersOrDefault(
      String key,
      String defaultValue);
  /**
   * <code>map&lt;string, string&gt; headers = 7;</code>
   */

  String getHeadersOrThrow(
      String key);
}

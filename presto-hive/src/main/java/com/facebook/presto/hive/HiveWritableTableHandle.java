/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.hive;

import com.facebook.presto.hive.metastore.HivePageSinkMetadata;
import com.facebook.presto.hive.metastore.SortingColumn;
import com.facebook.presto.spi.PrestoException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;

import static com.facebook.presto.spi.StandardErrorCode.GENERIC_USER_ERROR;
import static java.util.Objects.requireNonNull;

public class HiveWritableTableHandle
{
    private final String schemaName;
    private final String tableName;
    private final List<HiveColumnHandle> inputColumns;
    private final String filePrefix;

    private HivePageSinkMetadata pageSinkMetadata;
    private final LocationHandle locationHandle;
    private final Optional<HiveBucketProperty> bucketProperty;
    private final List<SortingColumn> preferredOrderingColumns;
    private final HiveStorageFormat tableStorageFormat;
    private final HiveStorageFormat partitionStorageFormat;
    private final HiveStorageFormat actualStorageFormat;
    private final HiveCompressionCodec compressionCodec;

    public HiveWritableTableHandle(
            String schemaName,
            String tableName,
            List<HiveColumnHandle> inputColumns,
            String filePrefix,
            HivePageSinkMetadata pageSinkMetadata,
            LocationHandle locationHandle,
            Optional<HiveBucketProperty> bucketProperty,
            List<SortingColumn> preferredOrderingColumns,
            HiveStorageFormat tableStorageFormat,
            HiveStorageFormat partitionStorageFormat,
            HiveStorageFormat actualStorageFormat,
            HiveCompressionCodec compressionCodec)
    {
        this.schemaName = requireNonNull(schemaName, "schemaName is null");
        this.tableName = requireNonNull(tableName, "tableName is null");
        this.inputColumns = ImmutableList.copyOf(requireNonNull(inputColumns, "inputColumns is null"));
        this.filePrefix = requireNonNull(filePrefix, "filePrefix is null");
        this.pageSinkMetadata = requireNonNull(pageSinkMetadata, "pageSinkMetadata is null");
        this.locationHandle = requireNonNull(locationHandle, "locationHandle is null");
        this.bucketProperty = requireNonNull(bucketProperty, "bucketProperty is null");
        this.preferredOrderingColumns = requireNonNull(preferredOrderingColumns, "preferredOrderingColumns is null");
        this.tableStorageFormat = requireNonNull(tableStorageFormat, "tableStorageFormat is null");
        this.partitionStorageFormat = requireNonNull(partitionStorageFormat, "partitionStorageFormat is null");
        this.actualStorageFormat = requireNonNull(actualStorageFormat, "actualStorageFormat is null");
        this.compressionCodec = requireNonNull(compressionCodec, "compressionCodec is null");

        if (!compressionCodec.isSupportedStorageFormat(actualStorageFormat)) {
            throw new PrestoException(GENERIC_USER_ERROR, String.format("%s compression is not supported with %s", compressionCodec.name(), actualStorageFormat.name()));
        }
    }

    @JsonProperty
    public String getSchemaName()
    {
        return schemaName;
    }

    @JsonProperty
    public String getTableName()
    {
        return tableName;
    }

    @JsonProperty
    public List<HiveColumnHandle> getInputColumns()
    {
        return inputColumns;
    }

    @JsonProperty
    public String getFilePrefix()
    {
        return filePrefix;
    }

    @JsonProperty
    public HivePageSinkMetadata getPageSinkMetadata()
    {
        return pageSinkMetadata;
    }

    @JsonProperty
    public LocationHandle getLocationHandle()
    {
        return locationHandle;
    }

    @JsonProperty
    public Optional<HiveBucketProperty> getBucketProperty()
    {
        return bucketProperty;
    }

    @JsonProperty
    public List<SortingColumn> getPreferredOrderingColumns()
    {
        return preferredOrderingColumns;
    }

    /* Use {@link #getActualStorageFormat()}*/
    @Deprecated
    @JsonProperty
    public HiveStorageFormat getTableStorageFormat()
    {
        return tableStorageFormat;
    }

    /* Use {@link #getActualStorageFormat()}*/
    @Deprecated
    @JsonProperty
    public HiveStorageFormat getPartitionStorageFormat()
    {
        return partitionStorageFormat;
    }

    /**
     * The actualStorageFormat is the real storage format that gets used later in the pipeline.
     * It could be either representing tableStorageFormat, or partitionStorageFormat.
     */
    @JsonProperty
    public HiveStorageFormat getActualStorageFormat()
    {
        return actualStorageFormat;
    }

    @JsonProperty
    public HiveCompressionCodec getCompressionCodec()
    {
        return compressionCodec;
    }

    @Override
    public String toString()
    {
        return schemaName + "." + tableName;
    }
}

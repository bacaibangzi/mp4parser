/*
 * Copyright 2009 castLabs GmbH, Berlin
 *
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an AS IS BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.coremedia.iso.boxes.fragment;

import com.coremedia.iso.IsoOutputStream;

import java.io.IOException;

/**
 * bit(6) reserved=0;
 * unsigned int(2) sample_depends_on;
 * unsigned int(2) sample_is_depended_on;
 * unsigned int(2) sample_has_redundancy;
 * bit(3) sample_padding_value;
 * bit(1) sample_is_difference_sample;
 * // i.e. when 1 signals a non-key or non-sync sample
 * unsigned int(16) sample_degradation_priority;
 */
public class SampleFlags {
  private int reserved;
  private int sampleDependsOn;
  private int sampleIsDependentOn;
  private int sampleHasRedundancy;
  private int samplePaddingValue;
  private boolean sampleIsDifferenceSample;
  private int sampleDegradationPriority;

  public SampleFlags(long flags) {
    reserved = (int) (flags >> 26);
    sampleDependsOn = (int) (flags >> 24) & 0x3;
    sampleIsDependentOn = (int) (flags >> 22) & 0x3;
    sampleHasRedundancy = (int) (flags >> 20) & 0x3;
    samplePaddingValue = (int) (flags >> 17) & 0x7;
    sampleIsDifferenceSample = ((flags >> 16) & 0x1) == 1;
    sampleDegradationPriority = (int) flags & 0xFFFF;
  }

    public int getReserved() {
        return reserved;
    }

    public int getSampleDependsOn() {
        return sampleDependsOn;
    }

    public int getSampleIsDependentOn() {
        return sampleIsDependentOn;
    }

    public int getSampleHasRedundancy() {
        return sampleHasRedundancy;
    }

    public int getSamplePaddingValue() {
        return samplePaddingValue;
    }

    public boolean isSampleIsDifferenceSample() {
        return sampleIsDifferenceSample;
    }

    public int getSampleDegradationPriority() {
        return sampleDegradationPriority;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("SampleFlags");
        sb.append("{reserved=").append(reserved);
        sb.append(", sampleDependsOn=").append(sampleDependsOn);
        sb.append(", sampleIsDependentOn=").append(sampleIsDependentOn);
        sb.append(", sampleHasRedundancy=").append(sampleHasRedundancy);
        sb.append(", samplePaddingValue=").append(samplePaddingValue);
        sb.append(", sampleIsDifferenceSample=").append(sampleIsDifferenceSample);
        sb.append(", sampleDegradationPriority=").append(sampleDegradationPriority);
        sb.append('}');
        return sb.toString();
    }

  public void getContent(IsoOutputStream os) throws IOException {
    long flags = reserved << 26;
    flags = flags | (sampleDependsOn << 24);
    flags = flags | (sampleHasRedundancy << 22);
    flags = flags | (samplePaddingValue << 19);
    flags = flags | ((sampleIsDifferenceSample ? 1 : 0) << 18);
    flags = flags | sampleDegradationPriority;
    os.writeUInt32(flags);
  }
}

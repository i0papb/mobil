private void fetchPrinterStatus() {
    KlipperApi api = ApiClient.getApi(this);
    // ask Moonraker for just the extruder and heater_bed status blocks
    api.queryObjects(new String[]{ "status" })
            .enqueue(new Callback<PrinterObjectsResponse>() {
                @Override
                public void onResponse(Call<PrinterObjectsResponse> call,
                                       Response<PrinterObjectsResponse> resp) {
                    if (resp.isSuccessful()
                            && resp.body() != null
                            && resp.body().result != null) {

                        PrinterObjectsResponse.Status st = resp.body().result.status;
                        if (st.extruder != null) {
                            extruderTemp.setText(
                                    getString(R.string.extruder_temp, st.extruder.temperature)
                            );
                        }
                        if (st.heaterBed != null) {
                            bedTemp.setText(
                                    getString(R.string.bed_temp, st.heaterBed.temperature)
                            );
                        }
                        Log.d(TAG, "Printer status updated via objects/query");
                    } else {
                        Log.w(TAG, "objects/query response not successful");
                    }
                }
                @Override
                public void onFailure(Call<PrinterObjectsResponse> call, Throwable t) {
                    Log.e(TAG, "Error querying objects", t);
                    Toast.makeText(MainActivity.this,
                            "Error fetching printer status", Toast.LENGTH_SHORT).show();
                }
            });
}

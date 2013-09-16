package com.paypal.asgard

import com.paypal.asgard.model.Snapshot

class SnapshotService {

    private static final String SNAPSHOTS = 'snapshots'

    def openStackRESTService

    def getAllSnapshots() {
        def resp = openStackRESTService.get(openStackRESTService.NOVA_VOLUME, "${SNAPSHOTS}/detail")
        def snapshots = []
        for (snapshot in resp.snapshots) {
            snapshots << new Snapshot(snapshot)
        }
        snapshots
    }

    def getSnapshotById(String snapshotId) {
        def resp = openStackRESTService.get(openStackRESTService.NOVA_VOLUME, "${SNAPSHOTS}/${snapshotId}")
        new Snapshot(resp.snapshot)
    }

    def deleteSnapshotById(String snapshotId) {
        openStackRESTService.delete(openStackRESTService.NOVA_VOLUME, "${SNAPSHOTS}/${snapshotId}")
    }

    def createSnapshot(def snapshot) {
        def body = [snapshot: [display_name: snapshot.name, force: false, display_description: snapshot.description,
                volume_id: snapshot.id]];
        openStackRESTService.post(openStackRESTService.NOVA_VOLUME, SNAPSHOTS, body);
    }

}
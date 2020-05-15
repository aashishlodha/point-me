package org.aashish.pointme.entity.v2;

import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "rooms")
public class Room extends AuditModel{

    @Id
    private String id;

    private String ownerId;
    
    private String ownerName;
    
    private String currentTopicId;
    
    @Transient
    private Map<String, List<String>> voteCounts;

}

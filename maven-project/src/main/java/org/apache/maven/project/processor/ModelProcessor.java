package org.apache.maven.project.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;

/*
 * hold original pom
 * Track where a property is from
 */
public class ModelProcessor
    extends BaseProcessor
{

    public ModelProcessor( Collection<Processor> processors )
    {
        super( processors );
    }

    public void process( Object parent, Object child, Object target, boolean isChildMostSpecialized )
    {
        super.process( parent, child, target, isChildMostSpecialized );

        Model c = (Model) child;
        Model t = (Model) target;
        Model p = null;
        if ( parent != null )
        {
            p = (Model) parent;
        }

        // Version
        if ( c.getVersion() == null )
        {
            if ( c.getParent() != null )
            {
                t.setVersion( c.getParent().getVersion() );
            }
        }
        else
        {
            t.setVersion( c.getVersion() );
        }

        // GroupId
        if ( c.getGroupId() == null )
        {
            if ( c.getParent() != null )
            {
                t.setGroupId( c.getParent().getGroupId() );
            }
        }
        else
        {
            t.setGroupId( c.getGroupId() );
        }

        t.setModelVersion( c.getModelVersion() );
        t.setPackaging( c.getPackaging() );

        if ( isChildMostSpecialized )
        {
            t.setName( c.getName() );
            t.setDescription( c.getDescription() );
        }

        if ( c.getInceptionYear() != null )
        {
            t.setInceptionYear( c.getInceptionYear() );
        }
        else if ( p != null )
        {
            t.setInceptionYear( p.getInceptionYear() );
        }
        
        List<Dependency> deps = new ArrayList<Dependency>();
        DependenciesProcessor dependenciesProcessor = new DependenciesProcessor();
        dependenciesProcessor.process( (p != null) ? p.getDependencies() : null, c.getDependencies(), deps, isChildMostSpecialized );
             
        if(deps.size() > 0)
        {
            t.getDependencies().addAll( deps );
        }  
        
        List<Dependency> mngDeps = new ArrayList<Dependency>();
        dependenciesProcessor.process( (p != null && p.getDependencyManagement() != null) ? p.getDependencyManagement().getDependencies(): null,
                        (c.getDependencyManagement() != null) ? c.getDependencyManagement().getDependencies(): null, mngDeps, isChildMostSpecialized );
        if(mngDeps.size() > 0)
        {
            if(t.getDependencyManagement() == null)
            {
                t.setDependencyManagement( new DependencyManagement() );    
            }
            t.getDependencyManagement().getDependencies().addAll( mngDeps );
        }
    }
}